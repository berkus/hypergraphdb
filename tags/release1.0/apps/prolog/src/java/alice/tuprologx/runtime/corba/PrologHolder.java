package alice.tuprologx.runtime.corba;

/**
* org/alice/tuprologx/runtime/corba/PrologHolder.java
* Generated by the IDL-to-Java compiler (portable), version "3.0"
* from org/alice/tuprologx/runtime/corba/Prolog.idl
* venerd� 28 dicembre 2001 12.37.09 GMT+01:00
*/

public final class PrologHolder implements org.omg.CORBA.portable.Streamable
{
  public alice.tuprologx.runtime.corba.Prolog value = null;

  public PrologHolder ()
  {
  }

  public PrologHolder (alice.tuprologx.runtime.corba.Prolog initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = alice.tuprologx.runtime.corba.PrologHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    alice.tuprologx.runtime.corba.PrologHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return alice.tuprologx.runtime.corba.PrologHelper.type ();
  }

}
