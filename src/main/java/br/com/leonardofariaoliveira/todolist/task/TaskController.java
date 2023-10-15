package br.com.leonardofariaoliveira.todolist.task;

import br.com.leonardofariaoliveira.todolist.user.UserModel;
import br.com.leonardofariaoliveira.todolist.utils.Utils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request){

        var user_id = request.getAttribute("user_id");
        task.setUser_id((UUID)user_id);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(task.getStart_at()) || currentDate.isAfter(task.getEnd_at())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Start date must be after current date");
        }

        if(task.getStart_at().isAfter(task.getEnd_at())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Start date must be before end date");
        }

        var taskCreated = this.taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);

    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var userId = request.getAttribute("userId");
        return this.taskRepository.findByUserId((UUID) userId);

    }

    @PutMapping("/{id}")
    public ResponseEntity update(
            @RequestBody TaskModel task,
            HttpServletRequest request,
            @PathVariable UUID id){

        var savedTask = this.taskRepository.findById(id).orElse(null);

        if(savedTask == null){

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Task not found");
        }

        if(!task.getUser_id().equals(request.getAttribute("userId"))){

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User has not permission to update this task");

        }

        Utils.copyNonNullProperties(task, savedTask);
        var updatedTask = this.taskRepository.save(savedTask);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }


}
