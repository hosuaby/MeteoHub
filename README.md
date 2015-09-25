# MeteoHub

Demo of Spring Integration on example of aggregator for meteorological data from
different providers.

## MeteoPlus

Individual devices make HTTP post directly on MeteoHub server.
Input format:
```javascript
{
    "id": "dc563c47-56da-4695-b5ac-5ebfd5af38fe",    // uuid
    "mesureDateTime": "2011-12-03T10:15:30+01:00[Europe/Paris]",
            // ISO_ZONED_DATE_TIME
    "coordinates": [
        48.8534100,     // latitude
        2.3488000       // longitude
    ],
    "temprature": 25,  // °celsius
    "humidity": 70.1,   // %
}
```