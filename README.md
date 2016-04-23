# MeteoHub

Demo of Spring Integration on example of aggregator for meteorological data from
different providers.

## MeteoPlus

Individual devices make HTTP post directly on MeteoHub server.
Input format:
```javascript
{
    "deviceId": "dc563c47-56da-4695-b5ac-5ebfd5af38fe",    // deviceId
    "mesureDateTime": "2011-12-03T10:15:30+01:00[Europe/Paris]",
            // ISO_ZONED_DATE_TIME
    "coordinates": [
        48.8534100,      // latitude
        2.3488000        // longitude
    ],
    "temperature": 25,   // °celsius
    "humidity": 70.1     // %
}
```

## LazySmog

MeteoHub makes regular GET requests to url `/lazysmog/{date}` to retrieve all
meteo records for provided date. Returned Json does not contain information
about individual devices.
```javascript
{
    "date": "2011-12-03",
    "temperature": [
        {
            "time": "15:30:17",
            "value": 298,15,     // ° Kelvin
            "coordinates": [
                48.8534100,      // latitude
                2.3488000        // longitude
            ],
            "zone": "Europe/Paris"
        }
        ...
    ],
    "humidity": [
        {
            "time": "15:30:17",
            "value": 68.8,       // %
            "coordinates": [
                48.8534100,      // latitude
                2.3488000        // longitude
            ],
            "zone": "Europe/Paris"
        }
        ...
    ]
}
```