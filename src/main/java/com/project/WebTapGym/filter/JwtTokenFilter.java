package com.project.WebTapGym.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        // Nếu là bypass token thì bỏ qua xác thực
        if (isBypassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy token từ header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid token");
            return;
        }

        try {
            String token = authHeader.substring(7);

            // Giải mã token (Placeholder để giải mã JWT)
            String userRole = getUserRoleFromToken(token);

            if (!isAuthorized(request, userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access denied");
                return;
            }

        } catch (Exception e) {
            System.out.println("JWT Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }

        // Cho phép yêu cầu tiếp tục nếu đã vượt qua kiểm tra
        filterChain.doFilter(request, response);
    }

    // Kiểm tra các URL và phương thức được bỏ qua xác thực
    private boolean isBypassToken(@NotNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/exercises", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/logout", apiPrefix), "POST"),
                Pair.of(String.format("%s/musclegroups", apiPrefix), "GET"),
                Pair.of(String.format("%s/products", apiPrefix), "GET")
        );

        for (Pair<String, String> bypassToken : bypassTokens) {
            if (request.getServletPath().contains(bypassToken.getFirst()) &&
                    request.getMethod().equalsIgnoreCase(bypassToken.getSecond())) {
                return true;
            }
        }

        return false;
    }

    // Kiểm tra quyền truy cập của người dùng
    private boolean isAuthorized(HttpServletRequest request, String userRole) {
        // Các endpoint chỉ cho phép ADMIN
        final List<Pair<String, String>> adminEndpoints = Arrays.asList(
                Pair.of(String.format("%s/products", apiPrefix), "POST"),
                Pair.of(String.format("%s/products", apiPrefix), "PUT"),
                Pair.of(String.format("%s/products", apiPrefix), "DELETE"),
                Pair.of(String.format("%s/users", apiPrefix), "GET"),
                Pair.of(String.format("%s/users", apiPrefix), "DELETE"),
                Pair.of(String.format("%s/users", apiPrefix), "PUT"),
                Pair.of(String.format("%s/musclegroups", apiPrefix), "POST"),
                Pair.of(String.format("%s/musclegroups", apiPrefix), "PUT"),
                Pair.of(String.format("%s/musclegroups", apiPrefix), "DELETE"),
                Pair.of(String.format("%s/exercises", apiPrefix), "POST"),
                Pair.of(String.format("%s/exercises", apiPrefix), "PUT"),
                Pair.of(String.format("%s/exercises", apiPrefix), "DELETE"),
                Pair.of(String.format("%s/categories", apiPrefix), "POST"),
                Pair.of(String.format("%s/categories", apiPrefix), "PUT"),
                Pair.of(String.format("%s/categories", apiPrefix), "DELETE"),
                Pair.of(String.format("%s/orders", apiPrefix), "GET"),
                Pair.of(String.format("%s/orders/{id}/status", apiPrefix), "PUT"),
                Pair.of(String.format("%s/orders/{id}/admin", apiPrefix), "DELETE")

        );
        // Các endpoint chỉ cho phép USER
        final List<Pair<String, String>> userEndpoints = Arrays.asList(
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories", apiPrefix), "GET"),
                Pair.of(String.format("%s/orders", apiPrefix), "POST"),
                Pair.of(String.format("%s/orders/user", apiPrefix), "GET"),
                Pair.of(String.format("%s/orders/{id}", apiPrefix), "PUT"),
                Pair.of(String.format("%s/orders/{id}", apiPrefix), "DELETE"),
                Pair.of(String.format("%s/musclegroups", apiPrefix), "GET"),
                Pair.of(String.format("%s/order_details", apiPrefix), "POST"),
                Pair.of(String.format("%s/order_details/", apiPrefix), "GET"),
                Pair.of(String.format("%s/order_details/order/", apiPrefix), "GET"),
                Pair.of(String.format("%s/chatbot", apiPrefix), "POST"),
                Pair.of(String.format("%s/food-analysis/analyze", apiPrefix), "POST"),
                Pair.of(String.format("%s/workout-schedules", apiPrefix), "POST"),
                Pair.of(String.format("%s/workout-schedules/user/", apiPrefix), "GET"),
                Pair.of(String.format("%s/workout-schedules/", apiPrefix), "POST"),
                Pair.of(String.format("%s/workout-schedules/suggest", apiPrefix), "GET"),
                Pair.of(String.format("%s/health-chat/", apiPrefix), "POST"),
                Pair.of(String.format("%s/health-chat/", apiPrefix), "GET"),
                Pair.of(String.format("%s/health-chat/add/", apiPrefix), "POST"),
                Pair.of(String.format("%s/health-chat/history/", apiPrefix), "GET"),
                Pair.of(String.format("%s/workout-status/complete/", apiPrefix), "POST"),
                Pair.of(String.format("%s/workout-status/user/", apiPrefix), "GET"),
                Pair.of(String.format("%s/notifications/", apiPrefix), "POST"),
                Pair.of(String.format("%s/post/", apiPrefix), "POST"),
                Pair.of(String.format("%s/post", apiPrefix), "GET"),
                Pair.of(String.format("%s/post/", apiPrefix), "GET"),
                Pair.of(String.format("%s/post/", apiPrefix), "PUT"),
                Pair.of(String.format("%s/post/", apiPrefix), "DELETE"),
                Pair.of(String.format("%s/comments", apiPrefix), "POST"),
                Pair.of(String.format("%s/comments/", apiPrefix), "PUT"),
                Pair.of(String.format("%s/comments/", apiPrefix), "GET"),
                Pair.of(String.format("%s/comments/", apiPrefix), "DELETE"),
                Pair.of(String.format("%s/likes/", apiPrefix), "POST"),
                Pair.of(String.format("%s/likes/", apiPrefix), "GET"),
                Pair.of(String.format("%s/likes/", apiPrefix), "PUT"),
                Pair.of(String.format("%s/likes/", apiPrefix), "DELETE")

        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        // Kiểm tra nếu là ADMIN endpoint
        for (Pair<String, String> endpoint : adminEndpoints) {
            if (requestPath.contains(endpoint.getFirst()) &&
                    requestMethod.equalsIgnoreCase(endpoint.getSecond())) {
                return userRole.equals("ROLE_ADMIN");
            }
        }

        // Kiểm tra nếu là USER endpoint
        for (Pair<String, String> endpoint : userEndpoints) {
            if (requestPath.contains(endpoint.getFirst()) &&
                    requestMethod.equalsIgnoreCase(endpoint.getSecond())) {
                return userRole.equals("ROLE_USER") || userRole.equals("ROLE_ADMIN");
            }
        }

        // Mặc định chặn nếu không xác định được quyền
        return false;
    }
    @Value("${jwt.secretKey}")
    private String secretKey;

    private String getUserRoleFromToken(String token) {
        try {
            // Tạo chuỗi key từ secretKey
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

            // Xác thực và giải mã token
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                    .setSigningKey(key)
                                    .build()
                                    .parseClaimsJws(token);

            // Trích xuất payload từ JWT
            Claims claims = claimsJws.getBody();

            // Trích xuất role dưới dạng chuỗi
            String role = claims.get("role", String.class);

            // Nếu role không tồn tại trong token, báo lỗi
            if (role == null || role.isEmpty()) {
                throw new RuntimeException("Role không tồn tại trong token");
            }

            // Chuẩn hóa role trong hệ thống (nếu cần)
            return role.equalsIgnoreCase("Admin") ? "ROLE_ADMIN" : "ROLE_USER";
        } catch (Exception e) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn", e);
        }
    }

}