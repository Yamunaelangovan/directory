package org.apache.ldap.server.jndi.request;

import javax.naming.Name;
import javax.naming.directory.Attributes;

public class ModifyRequest extends Request {

    private final Name name;
    private final int modOp;
    private final Attributes attributes;
    
    public ModifyRequest( Name name, int modOp, Attributes attributes )
    {
        if( name == null )
        {
            throw new NullPointerException( "name" );
        }
        if( attributes == null )
        {
            throw new NullPointerException( "attributes" );
        }
        
        this.name = name;
        this.modOp = modOp;
        this.attributes = attributes;
    }

    public Name getName()
    {
        return name;
    }
    
    public int getModOp()
    {
        return modOp;
    }
    
    public Attributes getAttributes()
    {
        return attributes;
    }
}
