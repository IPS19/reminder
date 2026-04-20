package com.reminder;

import com.reminder.model.ReminderRq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ReminderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReminderApplication.class, args);

		ReminderRq reminderRq = ReminderRq.builder()
				.title("new one reminder")
				.description("just description")
				.remind(LocalDateTime.now().plusDays(5))
				.build();
		ObjectMapper objectMapper = new ObjectMapper();
		String reminderRqJson = objectMapper.writeValueAsString(reminderRq);
		System.out.println(reminderRqJson);
	}

}
