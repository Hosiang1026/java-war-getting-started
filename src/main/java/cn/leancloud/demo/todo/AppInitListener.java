package cn.leancloud.demo.todo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.internal.impl.JavaRequestSignImplementation;

import cn.leancloud.LeanEngine;

@WebListener
public class AppInitListener implements ServletContextListener {

  private static final Logger logger = LogManager.getLogger(AppInitListener.class);

  private String appId = System.getenv("LEANCLOUD_APP_ID");
  private String appKey = System.getenv("LEANCLOUD_APP_KEY");
  private String appMasterKey = System.getenv("LEANCLOUD_APP_MASTER_KEY");
  private String appEnv = System.getenv("LEANCLOUD_APP_ENV");
  private String haveStaging = System.getenv("LEAN_CLI_HAVE_STAGING");

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {}

  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    logger.info("LeanEngine app init.");
    // 注册子类化
    AVObject.registerSubclass(Todo.class);

    if ("development".equals(appEnv) && "true".equals(haveStaging) || "stage".equals(appEnv)) {
      AVCloud.setProductionMode(false);
    }
    // 初始化AVOSCloud，请保证在整个项目中间只初始化一次
    LeanEngine.initialize(appId, appKey, appMasterKey);
    // 在请求签名中使用masterKey以激活云代码的最高权限
    JavaRequestSignImplementation.instance().setUseMasterKey(true);
    // 打开 debug 日志
    AVOSCloud.setDebugLogEnabled(false);
    // 向云引擎注册云函数
    LeanEngine.register(Cloud.class);
  }
}
