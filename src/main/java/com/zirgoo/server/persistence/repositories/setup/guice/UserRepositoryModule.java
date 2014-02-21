package com.zirgoo.server.persistence.repositories.setup.guice;

import com.google.inject.AbstractModule;
import com.zirgoo.server.persistence.repositories.UserRepository;
import com.zirgoo.server.persistence.repositories.setup.PlainSqlUserRepositoryImpl;
import com.zirgoo.server.persistence.setup.guice.PersistenceModule;

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
