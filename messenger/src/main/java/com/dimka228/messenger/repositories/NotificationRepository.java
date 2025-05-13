package com.dimka228.messenger.repositories;

import com.dimka228.messenger.entities.Notification;
import com.dimka228.messenger.entities.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>{
    @Transactional
	@Override
	void deleteById(Integer id);

    @Transactional
	void deleteByUserId(Integer id);

	Optional<Notification> findById(Integer id);
	List<Notification> findAllByUserId(Integer id);
	List<Notification> findAllByUserId(Integer id, Pageable page);


	List<Notification> findByUserIdAndType( Integer userId,  String type, Pageable page);
    
}
