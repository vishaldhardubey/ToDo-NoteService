/*package com.bridgeit.noteservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bridgeit.noteservice.utilservice.interceptor.ToDoInterceptor;

@Configuration
public class ToDoInterceptorConfig implements WebMvcConfigurer {

	@Autowired
	private ToDoInterceptor toDoInterceptor;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(toDoInterceptor).addPathPatterns("/notes/*");
	}
}
*/