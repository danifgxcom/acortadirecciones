package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.BlacklistToken;
import com.danifgx.acortadirecciones.persistence.repository.BlacklistTokenRepository;
import com.danifgx.acortadirecciones.service.BlacklistTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlacklistTokenServiceImpl implements BlacklistTokenService {

    @Autowired
    private BlacklistTokenRepository blacklistTokenRepository;

    @Override
    public void addToBlacklist(String token) {
        blacklistTokenRepository.save(new BlacklistToken(token));
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklistTokenRepository.existsById(token);
    }

    @Override
    public void removeFromBlacklist(String token) {
        blacklistTokenRepository.deleteById(token);
    }

    @Override
    public void purgeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<BlacklistToken> expiredTokens = blacklistTokenRepository.findAllByExpiryDateBefore(now);
        blacklistTokenRepository.deleteAll(expiredTokens);
    }
}