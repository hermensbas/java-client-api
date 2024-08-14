/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.pojo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Use this annotation in combination with {@link GeospatialLongitude} on a
 * sibling property to specify that a Geospatial Element Pair Index is needed
 * for these pojo properties. The value should follow the rules
 * for any Geospatial Element Pair Index in MarkLogic Server with coordinate
 * system "wgs84".
 *
 * This annotation can be associated with a public field:
 * <pre>    import com.marklogic.client.pojo.annotation.GeospatialLatitude;
 *    import com.marklogic.client.pojo.annotation.GeospatialLongitude;
 *    public class MyClass {
 *      <b>{@literal @}GeospatialLatitude</b>
 *      public String latitude;
 *      {@literal @}GeospatialLongitude
 *      public String longitude;
 *    }</pre>
 *
 * or with a public getter method:
 * <pre>
 *    public class MyClass {
 *      private String latitude;
 *      private String longitude;
 *      <b>{@literal @}GeospatialLatitude</b>
 *      public String getLatitude() {
 *        return latitude;
 *      }
 *      {@literal @}GeospatialLongitude
 *      public String getLongitude() {
 *        return longitude;
 *      }
 *      // ... setter methods ...
 *    }</pre>
*
 * or with a public setter method:
 * <pre>
 *    public class MyClass {
 *      private String latitude;
 *      private String longitude;
 *
 *      // ... getter methods ...
 *
 *      <b>{@literal @}GeospatialLatitude</b>
 *      public void setLatitude(String latitude) {
 *        this.latitude = latitude;
 *      }
 *      {@literal @}GeospatialLongitude
 *      public void setLongitude(String longitude) {
 *        this.longitude = longitude;
 *      }
 *    }</pre>
 * Run
 * {@link com.marklogic.client.pojo.util.GenerateIndexConfig} to generate
 * a package that can be used by administrators to create the indexes in
 * MarkLogic Server.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GeospatialLatitude {
}
