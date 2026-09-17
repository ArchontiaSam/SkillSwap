import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';



@Injectable({
  providedIn: 'root'
})

export class ApiService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  // ==========================================
  // 1. USERS CONTROLLER
  // ==========================================

  getAllUsers(): Observable<any> {
    return this.http.get(`${this.baseUrl}/users`);
  }

  getUserById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/users/${id}`);
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/users`, userData);
  }

  updateUser(id: number, userData: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/users/${id}`, userData);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/users/${id}`);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/login`, credentials);
  }

  // --- User Skills (connected with User Controller) ---
  getUserSkills(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/users/${userId}/skills`);
  }

  addUserSkill(userId: number, skillData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/${userId}/skills`, skillData);
  }

  deleteUserSkill(userSkillId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/users/skills/${userSkillId}`);
  }
  // ==========================================
  // 2. SKILLS CONTROLLER (Global/Master Skills)
  // ==========================================

  getAllSkills(): Observable<any> {
    return this.http.get(`${this.baseUrl}/skills`);
  }

  getSkillById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/skills/${id}`);
  }

  createSkill(skillData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/skills`, skillData);
  }

  updateSkill(id: number, skillData: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/skills/${id}`, skillData);
  }

  deleteGlobalSkill(id: number): Observable<any> {
    return this.http.delete<void>(`${this.baseUrl}/skills/${id}`);
  }

  // ==========================================
  // 3. EXCHANGES CONTROLLER
  // ==========================================

  createExchange(exchangeData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/exchanges`, exchangeData);
  }

  completeExchange(exchangeId: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/exchanges/${exchangeId}/complete`, {});
  }

  cancelExchange(exchangeId: number, userId: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/exchanges/${exchangeId}/cancel?userId=${userId}`, {});
  }

  deleteExchange(exchangeId: number, userId: number): Observable<any>{
    return this.http.delete(`${this.baseUrl}/exchanges/${exchangeId}?userId=${userId}`);
  }

  // ==========================================
  // 4. REVIEW CONTROLLER
  // ==========================================

  createReview(reviewData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/review`, reviewData);
  }

  getUserReviews(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/review/user/${userId}`);
  }

  // ==========================================
  // 5. GRAPH CONTROLLER
  // ==========================================

  getTrustDistance(fromUserId: number, toUserId: number): Observable<any> {
    const params = new HttpParams()
      .set('fromUserId', fromUserId.toString())
      .set('toUserId', toUserId.toString());
    return this.http.get(`${this.baseUrl}/v1/graph/trust-distance`, { params });
  }

  // ==========================================
  // 6. MATCHING CONTROLLER
  // ==========================================

  getDirectMatches(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/matches/direct/${userId}`);
  }

  getIndirectMatches(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/matches/indirect/${userId}`);
  }

  getUserExchanges(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/exchanges/user/${userId}`);
  }

  // ==========================================
  // 3-WAY EXCHANGES (Matching & Exchange extensions)
  // ==========================================
 createThreeWayExchange(matchData: any): Observable<any> {
  return this.http.post(`${this.baseUrl}/matches/three-way`, matchData);
}
}