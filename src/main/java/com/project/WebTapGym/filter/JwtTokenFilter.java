package com.project.WebTapGym.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    @Value("${api.prefix}")
//    private String apiPrefix;
//
//    @Override
//    protected void doFilterInternal(
//            @NotNull HttpServletRequest request,
//            @NotNull HttpServletResponse response,
//            @NotNull  FilterChain filterChain) throws ServletException, IOException
//    {
//        if(isBypassToken(request))
//        {
//            filterChain.doFilter(request, response);
//        }
//
//    }
//
//
//    private boolean isBypassToken(@NotNull HttpServletRequest request)
//    {
//        final List<Pair<String, String>> bypassTokens = Arrays.asList(
//                Pair.of(String.format("%s/exercises", apiPrefix), "GET"),
//                Pair.of(String.format("%s/musclegroups", apiPrefix), "GET"),
//                Pair.of(String.format("%s/musclegroups", apiPrefix), "POST"),
//                Pair.of(String.format("%s/muscle_main_groups", apiPrefix), "GET"),
//                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
//                Pair.of(String.format("%S/products", apiPrefix), "GET"),
//                Pair.of(String.format("%S/products", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/login", apiPrefix), "POST")
//
//
//        );
//        for(Pair<String, String> bypassToken : bypassTokens)
//        {
//            if(request.getServletPath().contains(bypassToken.getFirst()) &&
//                    request.getMethod().equals(bypassToken.getSecond())){
//                return true;
//            }
//        }
//
//        return false;
//    }
//}


@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        if (isBypassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // (Chỗ xử lý token sẽ thêm vào đây nếu bạn dùng lại sau)
        filterChain.doFilter(request, response);
    }

    private boolean isBypassToken(@NotNull HttpServletRequest request) {
        return true; // Luôn bỏ qua kiểm tra JWT (permit all)
    }
}
