package j.maps;

import org.infinispan.pojos.SerializablePojo;
import org.infinispan.test.SerializableTestPojo;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ExternallyMarshallableViaMap {

   private static final List<String> whiteListClasses = new ArrayList<>();

   static {
      whiteListClasses.add("Exception");
      whiteListClasses.add("$$Lambda$");
      whiteListClasses.add("java.lang.Class");
      whiteListClasses.add("java.time.Instant"); // prod
      whiteListClasses.add("org.hibernate.cache"); // prod
      whiteListClasses.add("org.hibernate.search.query.engine.impl.LuceneHSQuery"); // prod
      whiteListClasses.add("org.infinispan.distexec.RunnableAdapter"); // prod
      whiteListClasses.add("org.infinispan.jcache.annotation.DefaultCacheKey"); // prod
      whiteListClasses.add("org.infinispan.query.clustered.QueryResponse"); // prod
      whiteListClasses.add("org.infinispan.server.core.transport.NettyTransport$ConnectionAdderTask"); // prod
      whiteListClasses.add("org.infinispan.server.hotrod.CheckAddressTask"); // prod
      whiteListClasses.add("org.infinispan.server.infinispan.task.DistributedServerTask"); // prod
      whiteListClasses.add("org.infinispan.scripting.impl.DataType"); // prod
      whiteListClasses.add("org.infinispan.scripting.impl.DistributedScript");
      whiteListClasses.add("org.infinispan.stats.impl.ClusterCacheStatsImpl$DistributedCacheStatsCallable"); // prod
      whiteListClasses.add("org.infinispan.xsite.BackupSender$TakeSiteOfflineResponse"); // prod
      whiteListClasses.add("org.infinispan.xsite.BackupSender$BringSiteOnlineResponse"); // prod
      whiteListClasses.add("org.infinispan.xsite.XSiteAdminCommand$Status"); // prod
      whiteListClasses.add("org.infinispan.util.logging.events.EventLogLevel"); // prod
      whiteListClasses.add("org.infinispan.util.logging.events.EventLogCategory"); // prod
      whiteListClasses.add("java.util.Date"); // test
      whiteListClasses.add("java.lang.Byte"); // test
      whiteListClasses.add("java.lang.Integer"); // test
      whiteListClasses.add("java.lang.Double"); // test
      whiteListClasses.add("java.lang.Short"); // test
      whiteListClasses.add("java.lang.Long"); // test
      whiteListClasses.add("org.infinispan.test"); // test
      whiteListClasses.add("org.infinispan.server.test"); // test
      whiteListClasses.add("org.infinispan.it"); // test
      whiteListClasses.add("org.infinispan.all"); // test
      whiteListClasses.add("org.jboss.as.quickstarts.datagrid"); // quickstarts testing
   }

   private ExternallyMarshallableViaMap() {
      // Static class
   }

   public static boolean isAllowed(Object obj) {
      Class<?> clazz = obj.getClass();
      return isAllowed(clazz);
   }

   public static boolean isAllowed(Class<?> clazz) {
      Package pkg = clazz.getPackage();
      if (pkg == null) {
         if (clazz.isArray()) {
            return true;
         } else {
            throw new IllegalStateException("No package info for " + clazz + ", runtime-generated class?");
         }
      }
      String pkgName = pkg.getName();
      boolean isBlackList =
            Serializable.class.isAssignableFrom(clazz)
                  && isMarshallablePackage(pkgName)
                  && !isWhiteList(clazz.getName());
      return !isBlackList;
   }

   private static boolean isMarshallablePackage(String pkg) {
      return pkg.startsWith("java.")
            || pkg.startsWith("org.infinispan.")
            || pkg.startsWith("org.jgroups.")
            || pkg.startsWith("org.hibernate")
            || pkg.startsWith("org.apache")
            || pkg.startsWith("org.jboss")
            || pkg.startsWith("com.arjuna")
            ;
   }

   private static boolean isWhiteList(String className) {
      return whiteListClasses.stream().anyMatch(className::contains);
   }

   public static void main(String[] args) {
      System.out.println(ExternallyMarshallableViaMap.isAllowed(Instant.now()));
      System.out.println(ExternallyMarshallableViaMap.isAllowed(new SerializablePojo()));
      System.out.println(ExternallyMarshallableViaMap.isAllowed(new SerializableTestPojo()));
   }

}
