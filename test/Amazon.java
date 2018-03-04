String ACCESS_KEY_ID = "AKIA2E0A8F3B244C9986";
String SECRET_KEY = "7CE556A3BC234CC1FF9E8A5C324C0BB70AA21B6D";

AWSCredentials creds = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_KEY);
AmazonSimpleDBClient client = new AmazonSimpleDBClient(creds);