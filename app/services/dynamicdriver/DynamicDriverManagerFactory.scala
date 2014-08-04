package services.dynamicdriver

import java.net.URL
import java.util.ArrayList
import java.util.Collection
import java.util.List

object DynamicDriverManagerFactory {

   /**
     * Class loader just for the dynamic loading of a JDBC driver.
     * Also has the Chaperon class loaded into it.
     */
    var loader : ClassLoader = null;
    /**
     * Dynamically load JDBC drivers.
     * @param locations URLs containing JDBC driver code
     * @param className name of class to initialise
     *   (probably, but not necessarily the Driver)
     * @return a manager that can deregister and tidy up drivers
     */
    def create(locations : Collection[URL], className : String, chaperonJarFile : String ) : DynamicDriverManager = {
        // Set up class loaders.
        val urls : List[URL]  = new ArrayList[URL]();
        //// Unfortunately nested jars are busted, so this wont work.
        //urls.add(DynamicDriverManager.class.getResource(
        //    "chaperon.jar" // i.e. dynamicdriver/chaperon.jar
        //));
        val chaperonJar = new java.io.File(chaperonJarFile);

        if (!chaperonJar.exists()) {
            throw new ClassNotFoundException(
                "chaperon.jar not found"
            );
        }

        urls.add(chaperonJar.toURI().toURL());

        urls.addAll(locations);

        for ( i :Int <- 0 to urls.size() - 1) {
          val f:java.io.File = new java.io.File(urls.get(i).getPath())
          if (!f.exists())
            throw new ClassNotFoundException(
              f.getName + " not found"
            );

        }

        // Use bootclass loader as parent - don't use application classes.
        var loader  = java.net.URLClassLoader.newInstance(urls.toArray(Array()), null)
        
        return new DynamicDriverManager(loader, className);
    }
    
}