package com.xceptance.loadtest.posters.samplers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xceptance.xlt.api.engine.AbstractCustomSampler;
import com.xceptance.xlt.api.util.XltLogger;

/**
 * Custom sampler that fetches the current temperature from an external API.
 * 
 * @author Xceptance Software Technologies
 */
public class WeatherSampler extends AbstractCustomSampler
{
    /**
     * The API URL to fetch the weather data from.
     */
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=50.60&longitude=11.35&models=icon_seamless&current=temperature_2m";

    /**
     * Pattern to extract the temperature from the JSON response.
     * Example: "temperature_2m":12.3
     */
    private static final Pattern TEMPERATURE_PATTERN = Pattern.compile("\"temperature_2m\":\\s*(-?\\d+\\.?\\d*)");

    /**
     * The HTTP client used to perform the request.
     */
    private final HttpClient httpClient;

    /**
     * Constructor. Initializes the HTTP client.
     */
    public WeatherSampler()
    {
        this.httpClient = HttpClient.newBuilder()
                                    .connectTimeout(Duration.ofSeconds(5))
                                    .build();
    }

    /**
     * Executes the sampler logic: fetches weather data and returns the temperature.
     * 
     * @return the current temperature or 0.0 in case of an error
     */
    @Override
    public double execute()
    {
        try
        {
            // Create the request
            final HttpRequest request = HttpRequest.newBuilder()
                                                   .uri(URI.create(API_URL))
                                                   .GET()
                                                   .build();

            // Send the request and get the response
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
            {
                // Extract the temperature using regex
                final Matcher matcher = TEMPERATURE_PATTERN.matcher(response.body());
                if (matcher.find())
                {
                    return Double.parseDouble(matcher.group(1));
                }
                else
                {
                    XltLogger.runTimeLogger.warn("WeatherSampler: Could not find temperature in response: " + response.body());
                }
            }
            else
            {
                XltLogger.runTimeLogger.warn("WeatherSampler: Failed to fetch weather data. Status code: " + response.statusCode());
            }
        }
        catch (final Exception e)
        {
            XltLogger.runTimeLogger.error("WeatherSampler: Error while fetching weather data: " + e.getMessage());
        }

        return 0.0;
    }
}
