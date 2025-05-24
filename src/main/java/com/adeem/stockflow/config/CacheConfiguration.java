package com.adeem.stockflow.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.adeem.stockflow.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.adeem.stockflow.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.adeem.stockflow.domain.User.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Authority.class.getName());
            createCache(cm, com.adeem.stockflow.domain.User.class.getName() + ".authorities");
            createCache(cm, com.adeem.stockflow.domain.EntityAuditEvent.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Attachment.class.getName());
            createCache(cm, com.adeem.stockflow.domain.ClientAccount.class.getName());
            createCache(cm, com.adeem.stockflow.domain.ClientAccount.class.getName() + ".subscriptions");
            createCache(cm, com.adeem.stockflow.domain.ClientAccount.class.getName() + ".quotas");
            createCache(cm, com.adeem.stockflow.domain.Admin.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Admin.class.getName() + ".userRoles");
            createCache(cm, com.adeem.stockflow.domain.Customer.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Customer.class.getName() + ".addressLists");
            createCache(cm, com.adeem.stockflow.domain.Customer.class.getName() + ".carts");
            createCache(cm, com.adeem.stockflow.domain.Address.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Supplier.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Role.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Role.class.getName() + ".userRoles");
            createCache(cm, com.adeem.stockflow.domain.Role.class.getName() + ".rolePermissions");
            createCache(cm, com.adeem.stockflow.domain.Permission.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Permission.class.getName() + ".rolePermissions");
            createCache(cm, com.adeem.stockflow.domain.RolePermission.class.getName());
            createCache(cm, com.adeem.stockflow.domain.UserRole.class.getName());
            createCache(cm, com.adeem.stockflow.domain.PlanFormula.class.getName());
            createCache(cm, com.adeem.stockflow.domain.PlanFormula.class.getName() + ".planFeatures");
            createCache(cm, com.adeem.stockflow.domain.PlanFormula.class.getName() + ".resourceLimits");
            createCache(cm, com.adeem.stockflow.domain.PlanFeature.class.getName());
            createCache(cm, com.adeem.stockflow.domain.ResourceLimit.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Subscription.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Subscription.class.getName() + ".quotas");
            createCache(cm, com.adeem.stockflow.domain.Quota.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Product.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Product.class.getName() + ".images");
            createCache(cm, com.adeem.stockflow.domain.ProductFamily.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Inventory.class.getName());
            createCache(cm, com.adeem.stockflow.domain.InventoryTransaction.class.getName());
            createCache(cm, com.adeem.stockflow.domain.SaleOrder.class.getName());
            createCache(cm, com.adeem.stockflow.domain.SaleOrder.class.getName() + ".orderItems");
            createCache(cm, com.adeem.stockflow.domain.SaleOrderItem.class.getName());
            createCache(cm, com.adeem.stockflow.domain.PurchaseOrder.class.getName());
            createCache(cm, com.adeem.stockflow.domain.PurchaseOrder.class.getName() + ".orderItems");
            createCache(cm, com.adeem.stockflow.domain.PurchaseOrderItem.class.getName());
            createCache(cm, com.adeem.stockflow.domain.ReturnOrder.class.getName());
            createCache(cm, com.adeem.stockflow.domain.ReturnOrder.class.getName() + ".items");
            createCache(cm, com.adeem.stockflow.domain.ReturnOrderItem.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Cart.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Cart.class.getName() + ".cartItems");
            createCache(cm, com.adeem.stockflow.domain.CartItem.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Shipment.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Payment.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Payment.class.getName() + ".attachments");
            createCache(cm, com.adeem.stockflow.domain.PaymentReceipt.class.getName());
            createCache(cm, com.adeem.stockflow.domain.PaymentConfiguration.class.getName());
            createCache(cm, com.adeem.stockflow.domain.Product.class.getName() + ".inventories");
            createCache(cm, "inventoryStats");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
