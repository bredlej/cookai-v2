package com.fikafoodie.recipes.infrastructure.adapters.secondary.fake.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record PictureDTO(String picture) {
}
