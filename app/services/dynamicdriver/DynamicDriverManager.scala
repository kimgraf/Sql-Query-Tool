package services.dynamicdriver

import java.lang.reflect.Method
import java.sql.Driver
import java.sql.DriverManager
import java.util.Enumeration
import scala.collection.JavaConversions._

class DynamicDriverManager(l : ClassLoader,  className : String) {

 /**
    * Class loader just for the dynamic loading of a JDBC driver.
    * Also has the Chaperon class loaded into it.
    */
    val loader = l;

   /**
    * Get Chaperon class into loader and
    *   get reflected methods to call .
    */
    val chaperon : Class[_] = Class.forName("dynamicdriver.chaperon.Chaperon", true, loader);

    val getDrivers : Method = chaperon.getMethod("getDrivers");

    val getClassLoader : Method = chaperon.getMethod("getClassLoader", Class.forName("java.lang.Class"));

    def deregisterDriver : Method = chaperon.getMethod("deregisterDriver", Class.forName("java.sql.Driver"));

     var success = false;
     try {
         // Register driver in dynamic loader.
         Class.forName(className, true, loader);
              /// throws java.lang.ClassNotFoundException exc

         registerProxyDrivers();
         success = true;
     } finally {
         // Make some attempt not to leak in the case of exception.
         if (!success) {
             deregister();
         }
     }

   /**
    * For each Driver in the dynamic loader context,
    *   loader a proxy driver into this context.
    *   method in Chperon class that calls DriverManagerdriverManager
    */
   def registerProxyDrivers() = {
     val iter = getLoadedDrivers
     while(iter.hasNext){
       iter.next match {
         case driver : Driver => {
            if (isLoadedClassLoader(driver.getClass())) {
               var proxy : DriverProxy = new DriverProxy(driver);
               DriverManager.registerDriver(proxy);
            }
         }
         case _ =>
       }
     }
   }
   /**
    * Returns Drivers in {@link #loader} context.
    */
   private def getLoadedDrivers() :  Iterator[Any] = {
      val e  = getDrivers.invoke(chaperon)
     def en : Enumeration[_] = e match {
       case e : Enumeration[_]  => e;
       case _ => null
     }
     if(en == null) return null
     return enumerationAsScalaIterator(en)
   }
   /**
    * Indicates whether {@code clazz} was loaded by {@link #loader}.
    */
   private def isLoadedClassLoader(clazz : Class[_] ) : Boolean = {
       val clazzClassLoader = getClassLoader.invoke(chaperon, clazz);
       return clazzClassLoader == this.loader;
   }
   /**
    * Dergister Drivers.
    * Drivers in this context are dergistered.
    * Some cleanup of the dynamic class loader is required
    *    due to a memory leak in JDBC.
    */
   def deregister() : Unit = {
       // Deregister drivers in this context.
     val iter = getLoadedDrivers
     while(iter.hasNext){
        iter.next match {
         case proxy : DriverProxy => {
               var target : Driver = proxy.getTarget();
               if (isLoadedClassLoader(target.getClass())) {
                   // We need to make sure the class loader that loaded us
                   //   does not reference the driver dynamic loaded classes.
                   DriverManager.deregisterDriver(proxy);
                   // JDBC leaks...
                   deregisterDriver.invoke(chaperon, proxy.target );
               }
           }
         case _ =>
       }
     }
   }
}

