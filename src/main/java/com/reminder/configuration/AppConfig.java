package com.reminder.configuration;

import com.reminder.scheduler.ReminderSenderJob;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @Value("${spring.mail.host}")
    String mailHost;

    @Value("${spring.mail.port}")
    Integer port;

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.port}")
    String password;

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(ReminderSenderJob.class)
                .withIdentity("ReminderSenderJob", "group1")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity("myTrigger", "group1")
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")) // каждую минуту
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/15 * * * ?"))
                .build();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        return mailSender;
    }
}
