package com.bdpick.common;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Executor which useful for unit testing
 */
public class CurrentThreadExecutor implements ExecutorService {

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> f = new FutureTask<T>(task);
        f.run();
        return f;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        FutureTask<T> f = new FutureTask<T>(task, result);
        f.run();
        return f;
    }

    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<?> f = new FutureTask<Void>(task, null);
        f.run();
        return f;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return tasks.stream().map(this::submit).collect(Collectors.toList());
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return tasks.stream().map(this::submit).collect(Collectors.toList());
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return tasks.stream().map(this::submit).findFirst().get().get();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return tasks.stream().map(this::submit).findFirst().get().get();
    }

}