package org.semanticcloud.agents.provider.jclouds;

import org.semanticcloud.agents.provider.ProviderAgent;
import org.semanticcloud.providers.jclouds.JcloudsProvider;

public class JcloudsAgent extends ProviderAgent {

    @Override
    protected void init() {
        String uri = null;
        Object[] args = getArguments();
        providerId = args[0].toString();
        identity = args[1].toString();
        credential = args[2].toString();
        if(args.length == 4){
            uri = args[3].toString();
        }

//        if (providerId.equals("google-compute-engine")) {
//            System.out.println("dupa");
//            String fileContents = null;
//            try {
//                fileContents = Files.toString(new File(identity), Charset.defaultCharset());
//            } catch (IOException ex) {
//                System.out.println("Error Reading the Json key file. Please check the provided path is correct." + identity);
//                System.exit(1);
//            }
//            final JsonObject json = new JsonParser().parse(fileContents).getAsJsonObject();
//            identity = json.get("client_email").toString().replace("\"", "");
//            // When reading the file it reads in \n in as
//            credential = json.get("private_key").toString().replace("\"", "").replace("\\n", "\n");
//        }

        String ns2 = "http://semantic-cloud.org/" + providerId + "#";

        if( uri != null){
            provider = new JcloudsProvider(
                    ns2,
                    providerId,
                    identity,
                    credential,
                    uri
            );
        }
        else {
            provider = new JcloudsProvider(
                    ns2,
                    providerId,
                    identity,
                    credential
            );
        }

    }
}
