# nearestsukistore
Azure Function App that accepts user's geopoint and returns JSON-formatted details of Suki stores within 150 meters from a given geopoint.

URL open upon request to limit Azure budget

#### URL: https://nearestsukistore.azurewebsites.net/api/HttpExample?name=[Latitude],[Longitude]

Ex. https://nearestsukistore.azurewebsites.net/api/HttpExample?name=14.559260950531156,121.08307323787268

Outputs:
{"latitude":14.558772451263168,"longitude":121.0833584352468,"storeName":"[\"NenaSchoolSupplies\"]","distanceAwayMeters":62.17732620239258},{"latitude":14.558560728793498,"longitude":121.08239213419192,"storeName":"[\"MaizeCornYellow\"]","distanceAwayMeters":106.72483825683594},{"latitude":14.558505450401091,"longitude":121.08402743016885,"storeName":"[\"Marites Store\"]","distanceAwayMeters":132.52117919921875}]

#### Features:
Uses Weka Library to create a KDTree to be used for finding K-Nearest Neighbors of a Geopoint Instance
Uses Firebase Admin SDK to query Firestore DB

#### For local deployment and debugging:
Download our serviceAccountKey.json and move it under src/main/resources
Install maven, azure-cli, azure-functions-core-tools-4
Run mvn clean package
Run mvn azure-functions:run

