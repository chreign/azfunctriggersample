//package com.mine.sample;
//
//import com.microsoft.azure.functions.annotation.*;
//import com.microsoft.azure.functions.*;
//
///**
// * Azure Functions with Cosmos DB trigger.
// */
//public class CosmosDBTriggerSample {
//    /**
//     * This function will be invoked when there are inserts or updates in the specified database and collection.
//     */
//    @FunctionName("cosmosTriggerSample")
//    public void run(
//        @CosmosDBTrigger(
//            name = "items",
//            databaseName = "test",
//            collectionName = "collection",
//            leaseCollectionName="leases",
//            connectionStringSetting = "cosmosConnection",
//            createLeaseCollectionIfNotExists = false
//        )
//        Object[] items,
//        final ExecutionContext context
//    ) {
//        context.getLogger().info("Java Cosmos DB trigger function executed.");
//        context.getLogger().info("Documents count: " + items.length);
//    }
//}
