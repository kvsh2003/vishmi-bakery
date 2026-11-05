package lk.ijse.vishmibakehouse.dao;

import lk.ijse.vishmibakehouse.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {}

    public static DAOFactory getInstance() {
        if (daoFactory == null) {
            daoFactory = new DAOFactory();
        }
        return daoFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperDAO> T getDAO(DAOTypes types) {
        return switch (types) {
            case EMPLOYEE -> (T) new EmployeeDAOImpl();
            case USER_ACCOUNT -> (T) new UserAccountDAOImpl();
            case PRODUCT -> (T) new ProductDAOImpl();
            case CUSTOMER -> (T) new CustomerDAOImpl();
            case INGREDIENT -> (T) new IngredientDAOImpl();
            case SHOP -> (T) new ShopDAOImpl();
            case VEHICLE -> (T) new VehicleDAOImpl();
            case INGREDIENT_PURCHASE -> (T) new IngredientPurchaseDAOImpl();
            case PRODUCTION_BATCH -> (T) new ProductionBatchDAOImpl();
            case INGREDIENT_USAGE -> (T) new IngredientUsageDAOImpl();
            case SALE -> (T) new SaleDAOImpl();
            case SALE_ITEM -> (T) new SaleItemDAOImpl();
            case SPECIAL_ORDER -> (T) new SpecialOrderDAOImpl();
            case SPECIAL_ORDER_ITEM -> (T) new SpecialOrderItemDAOImpl();
            case LEFTOVER_BATCH -> (T) new LeftoverBatchDAOImpl();
            case BREAD_CRUMB_BATCH -> (T) new BreadCrumbBatchDAOImpl();
            case DELIVERY -> (T) new DeliveryDAOImpl();
            case DELIVERY_ITEM -> (T) new DeliveryItemDAOImpl();
            case QUERY -> (T) new QueryDAOImpl();
        };
    }
}
