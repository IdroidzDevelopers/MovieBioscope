package com.app.navajhalaka.util;

/**
 * Created by aarokiax on 2/16/2017.
 */

public interface AppInterface {
    String BASE_URL="http://139.59.62.165/";
    String BUS_REGISTRATION_API="api/fleets/auth";
    String ANALYTIC_API="api/analytics/";
    String COMMAND_ACK_API="api/transaction/ack";

    String BUS_REG_NO_KEY="regNo";
    String TRANSACTION_ID_KEY="transactionID";
    String FLEET_ID_KEY="fleetID";
    String ASSET_ID_KEY="assetID";
    String STATUS_KEY="status";
    int HANDLE_BUS_DETAILS =1;
    int HANDLE_REFRESH =2;
    String TYPE_VIDEO="video";
    String COMMAND_REFRESH ="REFRESH";
    String COMMAND_DOWNLOAD="DOWNLOAD";
    String COMMAND_UPDATE="UPDATE";
    String COMMAND_DELETE="DELETE";
    String COMMAND_SEQUENCE="SEQUENCE";
    String TYPE_ROUTE="route";
    String URI_KEY="uri";
    String ACTION_KEY="action";
    String DATA_KEY="data";
    String APP_KEY="app";
    String URL_KEY="urls";
}
