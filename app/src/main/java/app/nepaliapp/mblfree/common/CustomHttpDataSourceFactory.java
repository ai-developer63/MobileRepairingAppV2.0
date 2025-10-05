package app.nepaliapp.mblfree.common;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;

import java.util.HashMap;
import java.util.Map;

@UnstableApi
public class CustomHttpDataSourceFactory implements HttpDataSource.Factory {
    private final DefaultHttpDataSource.Factory factory;

    public CustomHttpDataSourceFactory(String jwtToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + jwtToken);

        factory = new DefaultHttpDataSource.Factory()
                .setDefaultRequestProperties(headers)
                .setAllowCrossProtocolRedirects(true);
    }

    @Override
    public HttpDataSource createDataSource() {
        return factory.createDataSource();
    }

    @Override
    public HttpDataSource.Factory setDefaultRequestProperties(Map<String, String> defaultRequestProperties) {
        return this;
    }
}

