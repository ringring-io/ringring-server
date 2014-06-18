package io.ringring.server.persistence.repositories.setup.guice;

import com.google.inject.AbstractModule;
import io.ringring.server.persistence.repositories.UserRepository;
import io.ringring.server.persistence.repositories.setup.PlainSqlUserRepositoryImpl;
import io.ringring.server.persistence.setup.guice.PersistenceModule;

/**
 * Created by kosztope on 12/02/14.
 */
public class UserRepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserRepository.class).to(PlainSqlUserRepositoryImpl.class);
        install(new PersistenceModule());
    }
}
