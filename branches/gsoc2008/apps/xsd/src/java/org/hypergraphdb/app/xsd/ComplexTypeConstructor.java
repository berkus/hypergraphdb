/*
 * This file is part of the HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import java.util.Iterator;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGHandleFactory;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGCompositeType;
import org.hypergraphdb.type.HGProjection;
import org.hypergraphdb.type.RecordType;
import org.hypergraphdb.HGValueLink;
import org.hypergraphdb.type.HGAtomType;
import java.util.Collection;
import org.hypergraphdb.atom.AtomProjection;
import org.hypergraphdb.atom.HGAtomRef;
import org.hypergraphdb.HGSearchResult;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.query.AtomTypeCondition;

/**
 * 
 */
public class ComplexTypeConstructor implements HGAtomType
{
   public static final HGPersistentHandle HANDLE = HGHandleFactory
         .makeHandle("bd99ff0c-5b32-11db-82a1-a78cc7527afc");
   public static final ComplexTypeConstructor INSTANCE = new ComplexTypeConstructor();

   private HyperGraph hg;

   /**
    * 
    */
   private ComplexTypeConstructor()
   {
   }

   /**
    * 
    * @param handle
    *           HGPersistentHandle
    * @param targetSet
    *           LazyRef
    * @param incidenceSet
    *           LazyRef
    * @return Object
    */
   public Object make(
      HGPersistentHandle handle, LazyRef<HGHandle[]> targetSet,
      IncidenceSetRef incidenceSet)
   {
      HGHandle[] handles = incidenceSet.deref();
      HGValueLink valueLink = (HGValueLink) hg.get(handles[0]);
      RecordType recordType = (RecordType) valueLink.getValue();

      Object result = null;

      Class clazz = ClassGenerator.generateComplexType(hg, ComplexTypeBase.class, recordType);
      try
      {
         result = clazz.newInstance();
      } catch (Exception e)
      {
         e.printStackTrace();
      }

      return result;
   }

   /**
    * 
    * @param hg
    *           HyperGraph
    */
   public void setHyperGraph(
      HyperGraph hg)
   {
      this.hg = hg;
   }

   /**
    * 
    * @param instance
    *           Object
    * @return HGPersistentHandle
    */
   public HGPersistentHandle store(
      Object instance)
   {
      // if(null == instance)
      {
         return HGHandleFactory.makeHandle();
      }
   }

   /**
    * 
    * @param handle
    *           HGPersistentHandle
    */
   public void release(
      HGPersistentHandle handle)
   {
      /** @todo */
   }

   /**
    * 
    * @param general
    *           Object
    * @param specific
    *           Object
    * @return boolean
    */
   public boolean subsumes(
      Object general, Object specific)
   {
      return false;
   }

} // ComplexTypeConstructor.