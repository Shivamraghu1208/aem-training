package com.adobe.aem.sample.site.core.services.impl;

import com.adobe.aem.sample.site.core.services.MoviesService;
import com.adobe.aem.sample.site.core.services.config.MoviesServiceConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = MoviesService.class, immediate = false)
@Designate(ocd= MoviesServiceConfiguration.class)
public class MoviesServiceImpl implements MoviesService {

    private String[] movies;
    @Override
    public String[] getMovies() {
        return movies;
    }

    @Activate
    @Modified
    protected void activate(MoviesServiceConfiguration configuration){
        movies = configuration.movies();
    }

}
