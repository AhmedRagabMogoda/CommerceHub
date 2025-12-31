package com.commercehub.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

@Repository
public class OrderNumberRepository {

	@Autowired
    private EntityManager entityManager;

    public Long getNextSequence() 
    {
        return ( (Number) entityManager.createNativeQuery("SELECT NEXT VALUE FOR order_number_seq").getSingleResult() )
        	   .longValue();
    }
}