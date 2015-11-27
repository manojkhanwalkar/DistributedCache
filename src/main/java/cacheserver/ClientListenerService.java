package cacheserver;

import server.Service;

public class ClientListenerService implements Service {



    String name ;


    @Override
    public void init() {



    }

    @Override
    public void destroy() {




    }

    @Override
    public void setName(String s) {

        name = s;

    }

    @Override
    public String getName() {
        return name;
    }
}
