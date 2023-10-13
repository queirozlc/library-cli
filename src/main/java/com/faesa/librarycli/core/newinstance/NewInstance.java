package com.faesa.librarycli.core.newinstance;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;

@ShellComponent
@RequiredArgsConstructor
public class NewInstance {

    private final InstanceRepository instanceRepository;
}
