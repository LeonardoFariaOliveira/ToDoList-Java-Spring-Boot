package br.com.leonardofariaoliveira.todolist.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "tb_task")
public class TaskModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(length = 50)
    private String title;

    private String description;

    private LocalDateTime start_at;

    private LocalDateTime end_at;

    private int priority;

    @CreationTimestamp
    private LocalDateTime created_at;


    private UUID user_id;

    public void setTitle(String title) throws Exception {
        if(title.length() > 50){
            throw new Exception("Title must have less than 50 chars");
        }
        this.title = title;
    }
}
