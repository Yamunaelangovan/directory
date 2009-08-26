/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.server.schema.bootstrap;


import javax.naming.NamingException;

import org.apache.directory.server.schema.bootstrap.ProducerTypeEnum;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.schema.registries.SyntaxCheckerRegistry;


/**
 * A simple Syntax factory for the core LDAP schema in Section 4.3.2 of
 * <a href="http://www.faqs.org/rfcs/rfc2252.html">RFC2252</a>.
 * The following table reproduced from RFC2252 shows the syntaxes included
 * within this SyntaxFactory:
 * <pre>
 * Index   Value being represented   H-R     OBJECT IDENTIFIER
 * =====================================================================
 * 0 ACI Item                         N  1.3.6.1.4.1.1466.115.121.1.1
 * 1 Access Point                     Y  1.3.6.1.4.1.1466.115.121.1.2
 * 2 Attribute Type Description       Y  1.3.6.1.4.1.1466.115.121.1.3
 * 3 Audio                            N  1.3.6.1.4.1.1466.115.121.1.4
 * 4 Binary                           N  1.3.6.1.4.1.1466.115.121.1.5
 * 5 Bit String                       Y  1.3.6.1.4.1.1466.115.121.1.6
 * 6 Boolean                          Y  1.3.6.1.4.1.1466.115.121.1.7
 * 7 Certificate                      N  1.3.6.1.4.1.1466.115.121.1.8
 * 8 Certificate List                 N  1.3.6.1.4.1.1466.115.121.1.9
 * 9 Certificate Pair                 N  1.3.6.1.4.1.1466.115.121.1.10
 * 10 Country String                  Y  1.3.6.1.4.1.1466.115.121.1.11
 * 11 DN                              Y  1.3.6.1.4.1.1466.115.121.1.12
 * 12 Data Quality Syntax             Y  1.3.6.1.4.1.1466.115.121.1.13
 * 13 Delivery Method                 Y  1.3.6.1.4.1.1466.115.121.1.14
 * 14 Directory String                Y  1.3.6.1.4.1.1466.115.121.1.15
 * 15 DIT Content Rule Description    Y  1.3.6.1.4.1.1466.115.121.1.16
 * 16 DIT Structure Rule Description  Y  1.3.6.1.4.1.1466.115.121.1.17
 * 17 DL Submit Permission            Y  1.3.6.1.4.1.1466.115.121.1.18
 * 18 DSA Quality Syntax              Y  1.3.6.1.4.1.1466.115.121.1.19
 * 19 DSE Type                        Y  1.3.6.1.4.1.1466.115.121.1.20
 * 20 Enhanced Guide                  Y  1.3.6.1.4.1.1466.115.121.1.21
 * 21 Facsimile Telephone Number      Y  1.3.6.1.4.1.1466.115.121.1.22
 * 22 Fax                             N  1.3.6.1.4.1.1466.115.121.1.23
 * 23 Generalized Time                Y  1.3.6.1.4.1.1466.115.121.1.24
 * 24 Guide                           Y  1.3.6.1.4.1.1466.115.121.1.25
 * 25 IA5 String                      Y  1.3.6.1.4.1.1466.115.121.1.26
 * 26 INTEGER                         Y  1.3.6.1.4.1.1466.115.121.1.27
 * 27 JPEG                            N  1.3.6.1.4.1.1466.115.121.1.28
 * 28 Master And Shadow Access Points Y  1.3.6.1.4.1.1466.115.121.1.29
 * 29 Matching Rule Description       Y  1.3.6.1.4.1.1466.115.121.1.30
 * 30 Matching Rule Use Description   Y  1.3.6.1.4.1.1466.115.121.1.31
 * 31 Mail Preference                 Y  1.3.6.1.4.1.1466.115.121.1.32
 * 32 MHS OR Address                  Y  1.3.6.1.4.1.1466.115.121.1.33
 * 33 Name And Optional UID           Y  1.3.6.1.4.1.1466.115.121.1.34
 * 34 Name Form Description           Y  1.3.6.1.4.1.1466.115.121.1.35
 * 35 Numeric String                  Y  1.3.6.1.4.1.1466.115.121.1.36
 * 36 Object Class Description        Y  1.3.6.1.4.1.1466.115.121.1.37
 * 37 OID                             Y  1.3.6.1.4.1.1466.115.121.1.38
 * 38 Other Mailbox                   Y  1.3.6.1.4.1.1466.115.121.1.39
 *
 * 39 Octet String                    Y  1.3.6.1.4.1.1466.115.121.1.40
 *
 * This is not going to be followed for OctetString which needs to be treated
 * as binary data.
 *
 * 40 Postal Address                  Y  1.3.6.1.4.1.1466.115.121.1.41
 * 41 Protocol Information            Y  1.3.6.1.4.1.1466.115.121.1.42
 * 42 Presentation Address            Y  1.3.6.1.4.1.1466.115.121.1.43
 * 43 Printable String                Y  1.3.6.1.4.1.1466.115.121.1.44
 * 44 Subtree Specification           Y  1.3.6.1.4.1.1466.115.121.1.45
 * 45 Supplier Information            Y  1.3.6.1.4.1.1466.115.121.1.46
 * 46 Supplier Or Consumer            Y  1.3.6.1.4.1.1466.115.121.1.47
 * 47 Supplier And Consumer           Y  1.3.6.1.4.1.1466.115.121.1.48
 * 48 Supported Algorithm             N  1.3.6.1.4.1.1466.115.121.1.49
 * 49 Telephone Number                Y  1.3.6.1.4.1.1466.115.121.1.50
 * 50 Teletex Terminal Identifier     Y  1.3.6.1.4.1.1466.115.121.1.51
 * 51 Telex Number                    Y  1.3.6.1.4.1.1466.115.121.1.52
 * 52 UTC Time                        Y  1.3.6.1.4.1.1466.115.121.1.53
 * 53 LDAP Syntax Description         Y  1.3.6.1.4.1.1466.115.121.1.54
 * 54 Modify Rights                   Y  1.3.6.1.4.1.1466.115.121.1.55
 * 55 LDAP Schema Definition          Y  1.3.6.1.4.1.1466.115.121.1.56
 * 56 LDAP Schema Description         Y  1.3.6.1.4.1.1466.115.121.1.57
 * 57 Substring Assertion             Y  1.3.6.1.4.1.1466.115.121.1.58
 * 58 Attribute Certificate Assertion N  1.3.6.1.4.1.1466.115.121.1.59
 * </pre>
 *
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SystemSyntaxProducer extends AbstractBootstrapProducer
{
    public SystemSyntaxProducer()
    {
        super( ProducerTypeEnum.SYNTAX_PRODUCER );
    }


    // ------------------------------------------------------------------------
    // BootstrapProducer Methods
    // ------------------------------------------------------------------------
    public void produce( Registries registries, ProducerCallback cb ) throws NamingException
    {
        BootstrapldapSyntax syntax;
        SyntaxCheckerRegistry syntaxCheckerRegistry = registries.getSyntaxCheckerRegistry();

        /*
         * From RFC 2252 Section 4.3.2. on Syntax Object Identifiers
         */

        /*
         * Value being represented        H-R OBJECT IDENTIFIER
         * ==================================================================
         * 0 ACI Item                        N  1.3.6.1.4.1.1466.115.121.1.1
         * 1 Access Point                    Y  1.3.6.1.4.1.1466.115.121.1.2
         * 2 Attribute Type Description      Y  1.3.6.1.4.1.1466.115.121.1.3
         * 3 Audio                           N  1.3.6.1.4.1.1466.115.121.1.4
         * 4 Binary                          N  1.3.6.1.4.1.1466.115.121.1.5
         * 5 Bit String                      Y  1.3.6.1.4.1.1466.115.121.1.6
         * 6 Boolean                         Y  1.3.6.1.4.1.1466.115.121.1.7
         * 7 Certificate                     N  1.3.6.1.4.1.1466.115.121.1.8
         * 8 Certificate List                N  1.3.6.1.4.1.1466.115.121.1.9
         * 9 Certificate Pair                N  1.3.6.1.4.1.1466.115.121.1.10
         */
        syntax = new BootstrapldapSyntax( SchemaConstants.ACI_ITEM_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "ACI Item" } );
        // This is in direct conflict with RFC 2252 but for us ACI Item is human readable
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.ACCESS_POINT_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Access Point" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.ATTRIBUT_TYPE_DESCRIPTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Attribute Type Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.AUDIO_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Audio" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.BINARY_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Binary" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.BIT_STRING_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Bit String" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.BOOLEAN_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Boolean" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.CERTIFICATE_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Certificate" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.CERTIFICATE_LIST_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Certificate List" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.CERTIFICATE_PAIR_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Certificate Pair" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        /*
         * Value being represented        H-R OBJECT IDENTIFIER
         * ===================================================================
         * 10 Country String                  Y  1.3.6.1.4.1.1466.115.121.1.11
         * 11 DN                              Y  1.3.6.1.4.1.1466.115.121.1.12
         * 12 Data Quality Syntax             Y  1.3.6.1.4.1.1466.115.121.1.13
         * 13 Delivery Method                 Y  1.3.6.1.4.1.1466.115.121.1.14
         * 14 Directory String                Y  1.3.6.1.4.1.1466.115.121.1.15
         * 15 DIT Content Rule Description    Y  1.3.6.1.4.1.1466.115.121.1.16
         * 16 DIT Structure Rule Description  Y  1.3.6.1.4.1.1466.115.121.1.17
         * 17 DL Submit Permission            Y  1.3.6.1.4.1.1466.115.121.1.18
         * 18 DSA Quality Syntax              Y  1.3.6.1.4.1.1466.115.121.1.19
         * 19 DSE Type                        Y  1.3.6.1.4.1.1466.115.121.1.20
         */
        syntax = new BootstrapldapSyntax( SchemaConstants.COUNTRY_STRING_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Country String" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DN_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "DN" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DATA_QUALITY_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Data Quality Syntax" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DELIVERY_METHOD_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Delivery Method" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DIRECTORY_STRING_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Directory String" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DIT_CONTENT_RULE_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "DIT Content Rule Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DIT_STRUCTURE_RULE_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "DIT Structure Rule Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DL_SUBMIT_PERMISSION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "DL Submit Permission" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DSA_QUALITY_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "DSA Quality Syntax" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.DSE_TYPE_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "DSE Type" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        /*
         * Value being represented        H-R OBJECT IDENTIFIER
         * ===================================================================
         * 20 Enhanced Guide                  Y  1.3.6.1.4.1.1466.115.121.1.21
         * 21 Facsimile Telephone Number      Y  1.3.6.1.4.1.1466.115.121.1.22
         * 22 Fax                             N  1.3.6.1.4.1.1466.115.121.1.23
         * 23 Generalized Time                Y  1.3.6.1.4.1.1466.115.121.1.24
         * 24 Guide                           Y  1.3.6.1.4.1.1466.115.121.1.25
         * 25 IA5 String                      Y  1.3.6.1.4.1.1466.115.121.1.26
         * 26 INTEGER                         Y  1.3.6.1.4.1.1466.115.121.1.27
         * 27 JPEG                            N  1.3.6.1.4.1.1466.115.121.1.28
         * 28 Master And Shadow Access Points Y  1.3.6.1.4.1.1466.115.121.1.29
         * 29 Matching Rule Description       Y  1.3.6.1.4.1.1466.115.121.1.30
         */
        syntax = new BootstrapldapSyntax( SchemaConstants.ENHANCED_GUIDE_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Enhanced Guide" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.FACSIMILE_TELEPHONE_NUMBER_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Facsimile Telephone Number" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.FAX_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Fax" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.GENERALIZED_TIME_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Generalized Time" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.GUIDE_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Guide" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.IA5_STRING_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "IA5 String" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.INTEGER_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "INTEGER" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.JPEG_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "JPEG" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.MASTER_AND_SHADOW_ACCESS_POINTS_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Master And Shadow Access Points" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.MATCHING_RULE_DESCRIPTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Matching Rule Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        /*
         * Value being represented        H-R OBJECT IDENTIFIER
         * ==================================================================
         * 30 Matching Rule Use Description   Y  1.3.6.1.4.1.1466.115.121.1.31
         * 31 Mail Preference                 Y  1.3.6.1.4.1.1466.115.121.1.32
         * 32 MHS OR Address                  Y  1.3.6.1.4.1.1466.115.121.1.33
         * 33 Name And Optional UID           Y  1.3.6.1.4.1.1466.115.121.1.34
         * 34 Name Form Description           Y  1.3.6.1.4.1.1466.115.121.1.35
         * 35 Numeric String                  Y  1.3.6.1.4.1.1466.115.121.1.36
         * 36 Object Class Description        Y  1.3.6.1.4.1.1466.115.121.1.37
         * 37 OID                             Y  1.3.6.1.4.1.1466.115.121.1.38
         * 38 Other Mailbox                   Y  1.3.6.1.4.1.1466.115.121.1.39
         * 39 Octet String                    Y  1.3.6.1.4.1.1466.115.121.1.40
         */
        syntax = new BootstrapldapSyntax( SchemaConstants.MATCHING_RULE_USE_DESCRIPTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Matching Rule Use Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.MAIL_PREFERENCE_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Mail Preference" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.MHS_OR_ADDRESS_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "MHS OR Address" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.NAME_AND_OPTIONAL_UID_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Name And Optional UID" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.NAME_FORM_DESCRIPTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Name Form Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.NUMERIC_STRING_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Numeric String" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.OBJECT_CLASS_DESCRIPTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Object Class Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.OID_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "OID" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.OTHER_MAILBOX_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Other Mailbox" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        /*
         * This is where we deviate.  An octet string may or may not be human readable.  Essentially
         * we are using this property of a syntax to determine if a value should be treated as binary
         * data or not.  It must be human readable always in order to get this property set to true.
         *
         * If we set this to true then userPasswords which implement this syntax are not treated as
         * binary attributes.  If that happens we can have data corruption due to UTF-8 handling.
         */
        syntax = new BootstrapldapSyntax( SchemaConstants.OCTET_STRING_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Octet String" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        /*
         * Value being represented        H-R OBJECT IDENTIFIER
         * ===================================================================
         * 40 Postal Address                  Y  1.3.6.1.4.1.1466.115.121.1.41
         * 41 Protocol Information            Y  1.3.6.1.4.1.1466.115.121.1.42
         * 42 Presentation Address            Y  1.3.6.1.4.1.1466.115.121.1.43
         * 43 Printable String                Y  1.3.6.1.4.1.1466.115.121.1.44
         * 44 Subtree Specification           Y  1.3.6.1.4.1.1466.115.121.1.45
         * 45 Supplier Information            Y  1.3.6.1.4.1.1466.115.121.1.46
         * 46 Supplier Or Consumer            Y  1.3.6.1.4.1.1466.115.121.1.47
         * 47 Supplier And Consumer           Y  1.3.6.1.4.1.1466.115.121.1.48
         * 48 Supported Algorithm             N  1.3.6.1.4.1.1466.115.121.1.49
         * 49 Telephone Number                Y  1.3.6.1.4.1.1466.115.121.1.50
         */
        syntax = new BootstrapldapSyntax( SchemaConstants.POSTAL_ADDRESS_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Postal Address" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.PROTOCOL_INFORMATION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Protocol Information" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.PRESENTATION_ADDRESS_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Presentation Address" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.PRINTABLE_STRING_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Printable String" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.SUBTREE_SPECIFICATION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Subtree Specification" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.SUPPLIER_INFORMATION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Supplier Information" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.SUPPLIER_OR_CONSUMER_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Supplier Or Consumer" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.SUPPLIER_AND_CONSUMER_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Supplier And Consumer" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.SUPPORTED_ALGORITHM_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Supported Algorithm" } );
        syntax.setHumanReadable( false );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.TELEPHONE_NUMBER_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Telephone Number" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        /*
         * Value being represented        H-R OBJECT IDENTIFIER
         * ==================================================================
         * 50 Teletex Terminal Identifier     Y  1.3.6.1.4.1.1466.115.121.1.51
         * 51 Telex Number                    Y  1.3.6.1.4.1.1466.115.121.1.52
         * 52 UTC Time                        Y  1.3.6.1.4.1.1466.115.121.1.53
         * 53 LDAP Syntax Description         Y  1.3.6.1.4.1.1466.115.121.1.54
         * 54 Modify Rights                   Y  1.3.6.1.4.1.1466.115.121.1.55
         * 55 LDAP BootstrapSchema Definition          Y  1.3.6.1.4.1.1466.115.121.1.56
         * 56 LDAP BootstrapSchema Description         Y  1.3.6.1.4.1.1466.115.121.1.57
         * 57 Substring Assertion             Y  1.3.6.1.4.1.1466.115.121.1.58
         */
        syntax = new BootstrapldapSyntax( SchemaConstants.TELETEX_TERMINAL_IDENTIFIER_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Teletex Terminal Identifier" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.TELEX_NUMBER_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Telex Number" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.UTC_TIME_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "UTC Time" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.LDAP_SYNTAX_DESCRIPTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "LDAP Syntax Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.MODIFY_RIGHTS_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Modify Rights" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.LDAP_SCHEMA_DEFINITION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "LDAP BootstrapSchema Definition" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.LDAP_SCHEMA_DESCRIPTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "LDAP BootstrapSchema Description" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new BootstrapldapSyntax( SchemaConstants.SUBSTRING_ASSERTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Substring Assertion" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );
        
        syntax = new BootstrapldapSyntax( SchemaConstants.ATTRIBUTE_CERTIFICATE_ASSERTION_SYNTAX, syntaxCheckerRegistry );
        syntax.setNames( new String[]
            { "Trigger Specification" } );
        syntax.setHumanReadable( true );
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );
    }
}
