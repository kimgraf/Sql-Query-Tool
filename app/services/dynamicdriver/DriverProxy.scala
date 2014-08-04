package services.dynamicdriver

import java.util.logging.Logger;
import java.sql.Driver
import java.sql.Connection
import java.sql.DriverPropertyInfo
import java.util.Properties


class DriverProxy( driver: java.sql.Driver) extends java.sql.Driver  {

   val target:  Driver = driver

   def getTarget() : Driver = target;

   def acceptsURL( url : String) : Boolean = target.acceptsURL(url);

   def connect( url : String, info : Properties ) : Connection = target.connect(url, info);
    
   def getMajorVersion() : Int = target.getMajorVersion();

   def getMinorVersion() : Int = target.getMinorVersion();

   def getPropertyInfo( url : String, info : Properties) : Array[DriverPropertyInfo] = target.getPropertyInfo(url, info);

   def jdbcCompliant() : Boolean = target.jdbcCompliant();

   
   override def getParentLogger() : Logger = null;
 
   override def toString() : String = "Proxy: "+target;

   override def hashCode() : Int = target.hashCode();
 
   override def equals(obj : Any) : Boolean = obj match {
       case obj : DriverProxy => this.target.equals(obj.target)
       case _ => false
   }

}