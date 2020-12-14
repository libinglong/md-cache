package com.sohu.smc;

import com.sohu.smc.md.cache.anno.MethodCacheConfig;
import com.sohu.smc.md.cache.spring.CacheProperty;
import org.junit.Test;
import org.reactivestreams.Subscription;
import org.springframework.beans.BeanUtils;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @author binglongli217932
 * <a href="mailto:libinglong9@gmail.com">libinglong:libinglong9@gmail.com</a>
 * @since 2020/9/17
 */
public class Test1 {
    {
        Hooks.onOperatorDebug();
    }

    private CountDownLatch latch = new CountDownLatch(1);

//    @Test
    public void fun() throws InterruptedException {

        Flux.interval(Duration.of(10, ChronoUnit.MILLIS))
                .onBackpressureDrop()
                .flatMap(aLong -> {
                    System.out.println("map:" + aLong);
                    return Mono.error(new RuntimeException("hehehhe"))
                            .doOnError(throwable -> System.out.println("error1234"))
                            .onErrorResume(throwable -> Mono.empty())
                            ;
                }, 4,7)
//                .onErrorResume(throwable -> Mono.empty())
                .subscribe(new BaseSubscriber<Object>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Object value) {
                        System.out.println("sub hookOnNext:" + value);
                        request(1);
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        System.out.println("sub error");
                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnComplete() {
                        System.out.println("sub complete");
                        request(1);
                    }
                });
//                .map(aLong -> {
//                    System.out.println(aLong);
//                    return aLong;
//                })
//                .flatMap(aLong -> {
//                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        return "s";
//                    });
//                    return Mono.fromCompletionStage(future)
////                            .timeout(Duration.of(5, ChronoUnit.MILLIS))
////                            .onErrorResume(throwable -> Mono.empty())
//                            ;
//                })
//                .subscribe(new BaseSubscriber<Object>() {
//                    @Override
//                    protected void hookOnSubscribe(Subscription subscription) {
//                        request(1);
//                    }
//
//                    @Override
//                    protected void hookOnNext(Object value) {
//                        super.hookOnNext(value);
//                        request(1);
//                    }
//                });
        latch.await();
    }



    @Test
//    @Prop(name = "a", value = "b")
    @MethodCacheConfig
    public void j() throws InterruptedException, ExecutionException, NoSuchMethodException {
        CacheProperty config = new CacheProperty();
        CacheProperty config1 = new CacheProperty();
        config1.setDelayInvalidTime(1);
        config1.setExpireTime(1);
        BeanUtils.copyProperties(config, config1);


        wrapper.convertIfNecessary();


        Arrays.stream(wrapper.getPropertyDescriptors())
                .forEach(propertyDescriptor -> {
                    String name = propertyDescriptor.getName();
                    Object propertyValue = wrapper.getPropertyValue(name);
                    System.out.println();
                });


        latch.await();
    }


}
