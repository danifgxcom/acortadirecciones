    package com.danifgx.acortadirecciones.persistence.dao.impl;

    import com.danifgx.acortadirecciones.entity.Url;
    import com.danifgx.acortadirecciones.persistence.dao.UrlDao;
    import com.danifgx.acortadirecciones.persistence.repository.UrlRepository;
    import org.springframework.stereotype.Repository;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;

@Repository
public class UrlDaoImpl implements UrlDao {

    private UrlRepository urlRepository;

    UrlDaoImpl (UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public Optional<Url> findByShortenedUrlId(String shortenedUrlId) {
        return urlRepository.findByShortenedUrlId(shortenedUrlId);
    }

    @Override
    public List<Url> findByExpiryDateBefore(LocalDateTime expiryDate) {
        return urlRepository.findByExpiryDateBefore(expiryDate);
    }

    @Override
    public Url save(Url url) {
        return urlRepository.save(url);
    }

    @Override
    public void deleteById(String shortenedUrlId) {
        urlRepository.deleteById(shortenedUrlId);
    }

    @Override
    public void deleteAll(List<Url> urls) {
        urlRepository.deleteAll(urls);
    }
}
