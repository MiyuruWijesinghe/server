package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.NotificationDao;
import bit.project.server.dao.UserDao;
import bit.project.server.entity.Branch;
import bit.project.server.entity.Branchstatus;
import bit.project.server.entity.Notification;
import bit.project.server.entity.User;
import bit.project.server.util.dto.PageQuery;
import bit.project.server.util.dto.ResourceLink;
import bit.project.server.util.exception.ConflictException;
import bit.project.server.util.exception.DataValidationException;
import bit.project.server.util.exception.NoPrivilegeException;
import bit.project.server.util.exception.ObjectNotFoundException;
import bit.project.server.util.helper.PersistHelper;
import bit.project.server.util.security.AccessControlManager;
import bit.project.server.util.validation.EntityValidator;
import bit.project.server.util.validation.ValidationErrorBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "dosend");
    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccessControlManager accessControlManager;

    @GetMapping("/latest")
    public List<Notification> latest(HttpServletRequest request) {
        User authUser = accessControlManager.authenticate(request);
        return notificationDao.findAllByUser(authUser, PageRequest.of(0, 4, DEFAULT_SORT)).getContent();
    }

    @GetMapping("/unread/count")
    public HashMap<String, Long> unreadcount(HttpServletRequest request) {
        User authUser = accessControlManager.authenticate(request);
        Long count = notificationDao.countByUserAndDoread(authUser, null);
        HashMap<String, Long> data = new HashMap<>();
        data.put("count", count);

        return data;
    }

    @GetMapping
    public Page<Notification> all(PageQuery pageQuery, HttpServletRequest request) {
        User authUser = accessControlManager.authenticate(request);
        return notificationDao.findAllByUser(authUser, PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), DEFAULT_SORT));
    }

    @GetMapping("/{id}")
    public Notification get(@PathVariable String id, HttpServletRequest request) {
        User authUser = accessControlManager.authenticate(request);
        Optional<Notification> optionalNotification = notificationDao.findById(id);
        if (optionalNotification.isEmpty()) throw new ObjectNotFoundException("Notification not found");
        Notification notification = optionalNotification.get();

        if (!notification.getUser().getId().equals(authUser.getId()))
            throw new NoPrivilegeException("You have no privilege to see others' notifications");

        return notification;
    }

    @PutMapping("/{id}/delivered")
    public void setDeliveredDate(@PathVariable String id, HttpServletRequest request) {
        User authUser = accessControlManager.authenticate(request);
        Optional<Notification> optionalNotification = notificationDao.findById(id);
        if (optionalNotification.isEmpty()) throw new ObjectNotFoundException("Notification not found");
        Notification notification = optionalNotification.get();

        if (!notification.getUser().getId().equals(authUser.getId()))
            throw new NoPrivilegeException("You have no privilege to update others' notifications");

        notification.setDodelivered(LocalDateTime.now());
        notificationDao.save(notification);
    }

    @PutMapping("/{id}/read")
    public void setReadDate(@PathVariable String id, HttpServletRequest request) {
        User authUser = accessControlManager.authenticate(request);
        Optional<Notification> optionalNotification = notificationDao.findById(id);
        if (optionalNotification.isEmpty()) throw new ObjectNotFoundException("Notification not found");
        Notification notification = optionalNotification.get();

        if (!notification.getUser().getId().equals(authUser.getId()))
            throw new NoPrivilegeException("You have no privilege to update others' notifications");

        notification.setDoread(LocalDateTime.now());
        notificationDao.save(notification);
    }

    @PostMapping("/user/{userId}")
    public ResourceLink add(@RequestBody Notification notification, @PathVariable String userId) {
        Optional<User> optionalUser = userDao.findById(Integer.parseInt(userId));
        if (!optionalUser.isPresent()) {
            throw new ObjectNotFoundException("User not found");
        }
        notification.setId(UUID.randomUUID().toString());
        notification.setUser(optionalUser.get());
        notification.setDosend(LocalDateTime.now());
        notification.setMessage(notification.getMessage());
        notificationDao.save(notification);
        return new ResourceLink(notification.getId(), "/notifications/" + notification.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        try {
            if (notificationDao.existsById(id)) {
                notificationDao.deleteById(id);
            }
        } catch (DataIntegrityViolationException | RollbackException e) {
            throw new ConflictException("Cannot delete. Because this notification already used in another module");
        }
    }
}
