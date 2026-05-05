package es.unex.cume.tw.resources;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return Set.of(UsersResource.class, RestaurantsResource.class, RestaurantDishesResource.class,
				DishesResource.class, OrdersResource.class, UserOrdersResource.class, ReviewsResource.class,
				SessionsResource.class, CategoriesResource.class, RestaurantCategoriesResource.class,
				UserFavoritesResource.class, RestaurantFavoritesResource.class, RestaurantSocialLinksResource.class);
	}
}
