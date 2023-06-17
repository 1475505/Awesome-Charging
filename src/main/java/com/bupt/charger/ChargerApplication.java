package com.bupt.charger;

import com.bupt.charger.util.SysTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChargerApplication {
	public static final Logger LOG = LoggerFactory.getLogger(ChargerApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ChargerApplication.class);
		Environment env = app.run(args).getEnvironment();
		LOG.info("启动成功： http://localhost:{}", env.getProperty("server.port") + "/v3/api-docs");

		SysTimer st = new SysTimer();
		st.startClock();
	}

}
