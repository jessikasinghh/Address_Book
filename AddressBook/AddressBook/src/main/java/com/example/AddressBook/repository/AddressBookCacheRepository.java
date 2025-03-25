package com.example.AddressBook.repository;

import com.example.AddressBook.model.AddressBookCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressBookCacheRepository extends CrudRepository<AddressBookCache, String> {
}
