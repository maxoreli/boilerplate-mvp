package com.tizisolutions.boilerplate_code.utils.rx;

import io.reactivex.Scheduler;

public interface SchedulerProvider {
    Scheduler ui();
    Scheduler computation();
    Scheduler io();
}
