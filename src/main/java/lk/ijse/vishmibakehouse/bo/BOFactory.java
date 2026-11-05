package lk.ijse.vishmibakehouse.bo;

import lk.ijse.vishmibakehouse.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getInstance() {
        if (boFactory == null) {
            boFactory = new BOFactory();
        }
        return boFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperBO> T getBO(BOTypes types) {
        return switch (types) {
            case AUTH -> (T) new AuthBOImpl();
            case EMPLOYEE -> (T) new EmployeeBOImpl();
            case PRODUCT -> (T) new ProductBOImpl();
            case INGREDIENT -> (T) new IngredientBOImpl();
            case PRODUCTION -> (T) new ProductionBOImpl();
            case SALE -> (T) new SaleBOImpl();
            case DELIVERY -> (T) new DeliveryBOImpl();
            case SPECIAL_ORDER -> (T) new SpecialOrderBOImpl();
            case LEFTOVER -> (T) new LeftoverBOImpl();
            case DASHBOARD -> (T) new DashboardBOImpl();
        };
    }
}
