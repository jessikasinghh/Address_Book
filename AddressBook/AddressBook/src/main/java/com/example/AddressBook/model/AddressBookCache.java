package com.example.AddressBook.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;

@RedisHash("AddressBook")  // ✅ Redis Storage
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressBookCache implements Serializable {
    @Id
    private String id;
    private String name;
    private String phone;
}
