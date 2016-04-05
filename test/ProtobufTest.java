import org.microcloud.manager.proto.SMOperatorInitParam.GeneralKey;
import org.microcloud.manager.proto.SMOperatorInitParam.SearchRequest;


public class ProtobufTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	 	SearchRequest.Builder builder = SearchRequest.newBuilder();
		
		GeneralKey.Builder keyBuilder = GeneralKey.newBuilder();
		keyBuilder.setName("file1.txt");
		builder.addGeneralKeys(keyBuilder.build());
		
		keyBuilder = GeneralKey.newBuilder();
		keyBuilder.setName("file2.txt");
		builder.addGeneralKeys(keyBuilder.build());
		
		keyBuilder = GeneralKey.newBuilder();
		keyBuilder.setName("file3.txt");
		builder.addGeneralKeys(keyBuilder.build());
		
		builder.setHost("localhost");
		builder.setPort(10001);
		builder.setDataSourceImplType(SearchRequest.DataSourceImplType.MONGO_GRIDFS);
		builder.setReadPreferenceType(SearchRequest.ReadPreferenceType.LIST_OF_KEYS);
		builder.setSourceName("filesystem");

    	String parameters2 = builder.build().toByteString().toStringUtf8();
    	org.microcloud.manager.logger.MyLogger.getInstance().log(parameters2);
	}

}
