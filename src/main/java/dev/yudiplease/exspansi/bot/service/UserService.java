package dev.yudiplease.exspansi.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.yudiplease.exspansi.bot.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements CrudService<UserInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String FILE_PATH = "users.json";
    private Map<String, UserInfo> users;
    private ObjectMapper objectMapper;

    public UserService() {
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        load();
    }

    @Override
    public void load() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                users = objectMapper.readValue(file, objectMapper.getTypeFactory()
                        .constructMapType(HashMap.class, String.class, UserInfo.class));
            } else {
                users = new HashMap<>();
            }
            LOGGER.info("Информация о пользователях успешно загружена");
        } catch (IOException e) {
            LOGGER.error(String.format("Не получилось загрузить пользователей из базы, причина: %s", e.getMessage()));
            users = new HashMap<>();
        }
    }

    @Override
    public void add(UserInfo user) {
        String staticId = user.getStaticId();
        if (!staticId.startsWith("#")) {
            users.put("#" + user.getStaticId(), user);
        } else {
            users.put(user.getStaticId(), user);
        }
        save();
    }

    @Override
    public UserInfo getById(String staticId) {
        load();
        if (!staticId.startsWith("#")) {
            return users.get("#" + staticId);
        } else {
            return users.get(staticId);
        }
    }

    @Override
    public void save() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), users);
            LOGGER.info("Данные о пользователе успешно сохранены.");
        } catch (IOException e) {
            LOGGER.error(String.format("Can't save users to json file, reason: %s", e.getMessage()));
        }
    }

    public void update(String oldStatic, UserInfo updatedUser) {
        UserInfo existingUser = users.get(oldStatic);
        if (existingUser != null) {
            if (updatedUser.getFullName() != null) {
                existingUser.setFullName(updatedUser.getFullName());
            }
            if (updatedUser.getFamilyRank() != null) {
                existingUser.setFamilyRank(updatedUser.getFamilyRank());
            }
            if (updatedUser.getStaticId() != null && !updatedUser.getStaticId().equals(oldStatic)) {
                if (!updatedUser.getStaticId().startsWith("#")) {

                }
                existingUser.setStaticId(updatedUser.getStaticId());
                users.remove(oldStatic);
                users.put(updatedUser.getStaticId(), existingUser);
            }
        }
        save();
    }

    @Override
    public void deleteById(String staticId) {
        if (!staticId.startsWith("#")) {
            String correctId = "#" + staticId;
        }
        users.remove(staticId);
        save();
    }
}
