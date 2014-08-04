if ( typeof String.prototype.endsWith != 'function' ) {
   String.prototype.endsWith = function( str ) {
     return this.substring( this.length - str.length, this.length ) === str;
   }
 };
