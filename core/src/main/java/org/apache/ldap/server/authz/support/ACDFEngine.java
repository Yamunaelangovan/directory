/*
 *   @(#) $Id$
 *   
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.ldap.server.authz.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.ldap.common.aci.ACIItem;
import org.apache.ldap.common.aci.ACITuple;
import org.apache.ldap.common.aci.AuthenticationLevel;
import org.apache.ldap.common.aci.MicroOperation;
import org.apache.ldap.common.exception.LdapNoPermissionException;
import org.apache.ldap.server.event.Evaluator;
import org.apache.ldap.server.event.ExpressionEvaluator;
import org.apache.ldap.server.interceptor.NextInterceptor;
import org.apache.ldap.server.schema.AttributeTypeRegistry;
import org.apache.ldap.server.schema.OidRegistry;
import org.apache.ldap.server.subtree.RefinementEvaluator;
import org.apache.ldap.server.subtree.RefinementLeafEvaluator;
import org.apache.ldap.server.subtree.SubtreeEvaluator;

public class ACDFEngine
{
    private final ACITupleFilter[] filters;
    
    public ACDFEngine( OidRegistry oidRegistry, AttributeTypeRegistry attrTypeRegistry ) throws NamingException
    {
        Evaluator entryEvaluator = new ExpressionEvaluator( oidRegistry, attrTypeRegistry );
        SubtreeEvaluator subtreeEvaluator = new SubtreeEvaluator( oidRegistry );
        RefinementEvaluator refinementEvaluator = new RefinementEvaluator(
                new RefinementLeafEvaluator( oidRegistry ) );
        
        filters = new ACITupleFilter[] {
                new RelatedUserClassFilter( subtreeEvaluator ),
                new RelatedProtectedItemFilter( attrTypeRegistry, refinementEvaluator, entryEvaluator ),
                new MaxValueCountFilter(),
                new MaxImmSubFilter(),
                new RestrictedByFilter(),
                new MicroOperationFilter(),
                new HighestPrecedenceFilter(),
                new MostSpecificUserClassFilter(),
                new MostSpecificProtectedItemFilter(),
        };
    }
    
    /**
     * Checks the user with the specified name can access the specified resource
     * (entry, attribute type, or attribute value) and throws {@link LdapNoPermissionException}
     * if the user doesn't have any permission to perform the specified grants.
     * 
     * @param next the next interceptor to the current interceptor
     * @param userGroupNames the DN of the group of the user who is trying to access the resource
     * @param username the DN of the user who is trying to access the resource
     * @param entryName the DN of the entry the user is trying to access 
     * @param attrId the attribute type of the attribute the user is trying to access.
     *               <tt>null</tt> if the user is not accessing a specific attribute type.
     * @param attrValue the attribute value of the attribute the user is trying to access.
     *                  <tt>null</tt> if the user is not accessing a specific attribute value.
     * @param microOperations the {@link MicroOperation}s to perform
     * @param aciTuples {@link ACITuple}s translated from {@link ACIItem}s in the subtree entries
     * @throws NamingException if failed to evaluate ACI items
     */
    public void checkPermission(
            NextInterceptor next,
            Collection userGroupNames, Name username, AuthenticationLevel authenticationLevel,
            Name entryName, String attrId, Object attrValue,
            Collection microOperations, Collection aciTuples ) throws NamingException 
    {
        if( !hasPermission(
                next,
                userGroupNames, username, authenticationLevel,
                entryName, attrId, attrValue,
                microOperations, aciTuples ) )
        {
            throw new LdapNoPermissionException();
        }
    }
    
    /**
     * Returns <tt>true</tt> if the user with the specified name can access the specified resource
     * (entry, attribute type, or attribute value) and throws {@link LdapNoPermissionException}
     * if the user doesn't have any permission to perform the specified grants.
     * 
     * @param next the next interceptor to the current interceptor 
     * @param userGroupNames the DN of the group of the user who is trying to access the resource
     * @param userName the DN of the user who is trying to access the resource
     * @param entryName the DN of the entry the user is trying to access 
     * @param attrId the attribute type of the attribute the user is trying to access.
     *               <tt>null</tt> if the user is not accessing a specific attribute type.
     * @param attrValue the attribute value of the attribute the user is trying to access.
     *                  <tt>null</tt> if the user is not accessing a specific attribute value.
     * @param microOperations the {@link MicroOperation}s to perform
     * @param aciTuples {@link ACITuple}s translated from {@link ACIItem}s in the subtree entries
     */
    public boolean hasPermission(
            NextInterceptor next, 
            Collection userGroupNames, Name userName, AuthenticationLevel authenticationLevel,
            Name entryName, String attrId, Object attrValue,
            Collection microOperations, Collection aciTuples ) throws NamingException
    {
        if( entryName == null )
        {
            throw new NullPointerException( "entryName" );
        }
        
        Attributes userEntry = next.lookup( userName );
        Attributes entry = next.lookup( entryName );
        
        // Determine the scope of the requested operation.
        OperationScope scope;
        if( attrId == null )
        {
            scope = OperationScope.ENTRY;
        }
        else if( attrValue == null )
        {
            scope = OperationScope.ATTRIBUTE_TYPE;
        }
        else
        {
            scope = OperationScope.ATTRIBUTE_TYPE_AND_VALUE;
        }
        
        // Clone aciTuples in case it is unmodifiable.
        aciTuples = new ArrayList( aciTuples );

        // Filter unrelated and invalid tuples
        for( int i = 0; i < filters.length; i++ )
        {
            ACITupleFilter filter = filters[ i ];
            aciTuples = filter.filter(
                    aciTuples, scope, next,
                    userGroupNames, userName, userEntry, authenticationLevel,
                    entryName, attrId, attrValue, entry, microOperations );
        }
        
        // Deny access if no tuples left.
        if( aciTuples.size() == 0 )
        {
            return false;
        }

        // Grant access if and only if one or more tuples remain and
        // all grant access. Otherwise deny access.
        for( Iterator i = aciTuples.iterator(); i.hasNext(); )
        {
            ACITuple tuple = ( ACITuple ) i.next();
            if( !tuple.isGrant() )
            {
                return false;
            }
        }

        return true;
    }
}
