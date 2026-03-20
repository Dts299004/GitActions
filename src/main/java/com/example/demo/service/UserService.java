package com.example.demo.service;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Service
@Transactional
public class UserService implements UserDetailsService {
 @Autowired
 private UserRepository userRepository;
 @Autowired
 private RoleRepository roleRepository;
 // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu.
 public void save(@NotNull User user) {
 user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
 userRepository.save(user);
 }
 // Cập nhật thông tin người dùng (không mã hóa lại mật khẩu)
 public void updateUserInfo(@NotNull User user) {
 userRepository.save(user);
 }
 // Gán vai trò cho người dùng.
 public void setRole(String username, String roleName) {
 userRepository.findByUsername(username).ifPresentOrElse(
 user -> {
 String formattedRoleName = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
 Role role = roleRepository.findByName(formattedRoleName).orElse(null);
 if (role != null) {
 user.getRoles().add(role);
 userRepository.save(user);
 }
 },
 () -> { throw new UsernameNotFoundException("User not found"); }
 );
 }
 // Gán vai trò mặc định cho người dùng dựa trên tên người dùng.
 public void setDefaultRole(String username) {
 setRole(username, "USER");
 }
 // Tải thông tin chi tiết người dùng để xác thực.
 @Override
 public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
 var user = userRepository.findByUsername(username)
 .orElseThrow(() -> new UsernameNotFoundException("User not found"));
 return org.springframework.security.core.userdetails.User
 .withUsername(user.getUsername())
 .password(user.getPassword())
 .authorities(user.getAuthorities())
 .accountExpired(!user.isAccountNonExpired())
 .accountLocked(!user.isAccountNonLocked())
 .credentialsExpired(!user.isCredentialsNonExpired())
 .disabled(!user.isEnabled())
 .build();
 }
 // Tìm kiếm người dùng dựa trên tên đăng nhập.
 public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
 return userRepository.findByUsername(username);
 }
 // Kiểm tra email đã tồn tại.
 public boolean existsByEmail(String email) {
 return userRepository.existsByEmail(email);
 }
}
