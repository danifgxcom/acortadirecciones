package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.UrlLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlLogRepository extends MongoRepository<UrlLog, String> {}
