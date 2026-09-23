import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {

  // ==========================================
  // TABS
  // ==========================================
  activeTab: 'skills' | 'browse' | 'matches' | 'trust' | 'exchanges' | 'profile' = 'skills';

  setTab(tab: typeof this.activeTab) {
    this.activeTab = tab;
    if (tab === 'browse' && this.allSkills.length === 0) this.loadAllSkills();
    if (tab === 'matches') this.loadMatches();
    if (tab === 'exchanges') { this.loadMyExchanges(); this.loadMyReviews(); }
    if (tab === 'trust' && this.allUsers.length === 0) this.loadAllUsers();
  }

  // ==========================================
  // CURRENT USER
  // ==========================================
  currentUser: {id: number; name: string; email: string; rating?: number } | null = null;

  constructor(
    private apiService: ApiService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadUserSkills();
    this.loadMyRating();
  }

  loadUserData() {
    const idStr = localStorage.getItem('userId');
    this.currentUser = {
      id: idStr ? parseInt(idStr, 10) : 0,
      name: localStorage.getItem('userName') || 'User',
      email: localStorage.getItem('userEmail') || 'user@skillswap.com'
    };
    this.profileForm = {
      name: this.currentUser.name,
      email: this.currentUser.email
    };
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  // ==========================================
  // MY SKILLS
  // ==========================================
  newSkillName: string = '';
  newSkillCategory: string = '';
  newSkillType: string = 'OFFERED';
  userSkills: any[] = [];
  errorMessage: string = '';

  isEditing: boolean = false;
  editingSkillId: number | null = null;

  loadUserSkills() {
    const userIdStr = localStorage.getItem('userId');
    if (!userIdStr) return;
    const userId = parseInt(userIdStr, 10);

    this.apiService.getUserSkills(userId).subscribe({
      next: (skills) => {
        this.userSkills = [...skills];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error loading skills:', err)
    });
  }

  addSkill() {
    if (!this.newSkillName || !this.newSkillName.trim()) {
      this.errorMessage = 'Το όνομα skill δεν μπορεί να είναι κενό.';
      return;
    }
    const userIdStr = localStorage.getItem('userId');
    if (!userIdStr) return;
    const userId = parseInt(userIdStr, 10);

    const skillPayload = {
      skillName: this.newSkillName.trim(),
      category: (this.newSkillCategory && this.newSkillCategory.trim()) || 'General',
      type: this.newSkillType
    };

    this.apiService.addUserSkill(userId, skillPayload).subscribe({
      next: () => {
        this.errorMessage = '';
        this.clearForm();
        this.loadUserSkills();
      },
      error: (error: any) => {
        console.error('Error adding skill:', error);
        this.errorMessage = error.error?.message || 'Failed to add skill.';
      }
    });
  }

  saveSkill() {
    if (this.isEditing && this.editingSkillId !== null) {
      const userIdStr = localStorage.getItem('userId');
      if (!userIdStr) return;
      const userId = parseInt(userIdStr, 10);

      this.apiService.deleteUserSkill(this.editingSkillId).subscribe({
        next: () => {
          const updatedSkillPayload = {
            skillName: this.newSkillName.trim(),
            category: (this.newSkillCategory && this.newSkillCategory.trim()) || 'General',
            type: this.newSkillType
          };
          this.apiService.addUserSkill(userId, updatedSkillPayload).subscribe({
            next: () => {
              this.clearForm();
              this.loadUserSkills();
            },
            error: (err) => {
              console.error('Failed to add updated skill', err);
              this.errorMessage = 'Failed to update skill.';
            }
          });
        },
        error: (err) => {
          console.error('Failed to delete old skill for update', err);
          this.errorMessage = 'Failed to update skill.';
        }
      });
    } else {
      this.addSkill();
    }
  }

  deleteSkill(userSkillId: number) {
    this.apiService.deleteUserSkill(userSkillId).subscribe({
      next: () => this.loadUserSkills(),
      error: (err) => {
        this.errorMessage = 'Failed to delete Skill';
        console.error(err);
      }
    });
  }

  startEdit(skill: any) {
    this.isEditing = true;
    this.editingSkillId = skill.id;
    this.newSkillName = skill.skillName;
    this.newSkillCategory = skill.category || '';
    this.newSkillType = skill.type;
  }

  clearForm() {
    this.isEditing = false;
    this.editingSkillId = null;
    this.newSkillName = '';
    this.newSkillCategory = '';
  }

  // ==========================================
  // BROWSE ALL SKILLS
  // ==========================================
  allSkills: any[] = [];
  skillsLoading = false;

  loadAllSkills() {
    this.skillsLoading = true;
    this.apiService.getAllSkills().subscribe({
      next: (skills) => {
        this.allSkills = skills;
        this.skillsLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading all skills:', err);
        this.skillsLoading = false;
      }
    });
  }

  // ==========================================
  // MATCHING (direct + 3-way)
  // ==========================================
  directMatches: any[] = [];
  indirectMatches: any[] = [];
  matchesLoading = false;
  proposedKeys = new Set<string>();
   proposedThreeWayKeys = new Set<string>();

  threeWayKey(m: any): string {
    return `3way-${m.userAId}-${m.userBId}-${m.userCId}`;
  }


  loadMatches() {
    if (!this.currentUser?.id) return;
    this.matchesLoading = true;

    this.apiService.getDirectMatches(this.currentUser.id).subscribe({
      next: (res) => {
        this.directMatches = res;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error loading direct matches:', err)
    });

    this.apiService.getIndirectMatches(this.currentUser.id).subscribe({
      next: (res) => {
        this.indirectMatches = res;
        this.matchesLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading indirect matches:', err);
        this.matchesLoading = false;
      }
    });
  }

  matchKey(m: any): string {
    return `${m.matchedUserId}-${m.offeredSkillId}-${m.requestedSkillId}`;
  }

  proposeExchange(match: any) {
    if (!this.currentUser?.id) return;
    const key = this.matchKey(match);
    if (this.proposedKeys.has(key)) return;
    this.proposedKeys.add(key);

    const payload = {
      requesterId: this.currentUser.id,
      providerId: match.matchedUserId,
      offeredSkillId: match.requestedSkillId,   // my skill
      requestedSkillId: match.offeredSkillId    // others skill
    };
    
    this.apiService.createExchange(payload).subscribe({
            next: (res: any) => {
        this.showExchangeMessage(`Exchange #${res.id} created (PENDING).`);
        this.loadMyExchanges();
      },

      error: (err) => {
        this.proposedKeys.delete(key);
        console.error('Error creating exchange:', err);
        this.exchangeMessage = 'Failed to create exchange: ' + (err.error?.message || err.message);
        this.cdr.detectChanges();
      }
    });
  }
    // ==========================================
    // TRUST DISTANCE
    // ==========================================
    allUsers: any[] = [];
    trustTargetId: number | null = null;
    trustResult: number | null = null;
    trustError: string = '';

    loadAllUsers() {
      this.apiService.getAllUsers().subscribe({
        next: (users) => {
          this.allUsers = users.filter((u: any) => u.id !== this.currentUser?.id);
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error loading users:', err)
      });
    }

    checkTrustDistance() {
      this.trustError = '';
      this.trustResult = null;
      if (!this.currentUser?.id || !this.trustTargetId) {
        this.trustError = 'Pick a user.';
        return;
      }
      this.apiService.getTrustDistance(this.currentUser.id, this.trustTargetId).subscribe({
        next: (res: number) => {
          this.trustResult = res;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error fetching trust distance:', err);
          this.trustError = 'Failed to measure trust distance.';
        }
      });
    }

  // ==========================================
  // EXCHANGES
  // ==========================================
    myExchanges: any[] = [];
    exchangeMessage: string = '';
    exchangeStatusFilter: 'ALL' | 'PENDING' | 'COMPLETED' | 'CANCELLED' = 'ALL';
    exchangeDisplayLimit = 5;

    get filteredExchanges(): any[] {
      if (this.exchangeStatusFilter === 'ALL') return this.myExchanges;
      return this.myExchanges.filter(e => e.status === this.exchangeStatusFilter);
    }

    get visibleExchanges(): any[] {
      return this.filteredExchanges.slice(0, this.exchangeDisplayLimit);
    }

    setExchangeFilter(status: typeof this.exchangeStatusFilter) {
      this.exchangeStatusFilter = status;
      this.exchangeDisplayLimit = 5;
    }

    showMoreExchanges() {
      this.exchangeDisplayLimit += 5;
    }

    private showExchangeMessage(msg: string) {
    this.exchangeMessage = msg;
    this.cdr.detectChanges();
    setTimeout(() => {
      this.exchangeMessage = '';
      this.cdr.detectChanges();
    }, 4000);
  }

  loadMyExchanges() {
    if (!this.currentUser?.id) return;
    this.apiService.getUserExchanges(this.currentUser.id).subscribe({
      next: (res) => {  this.myExchanges = res.sort((a: any, b: any) => b.id - a.id);
      this.cdr.detectChanges();
      },
      error: (err) => console.error('Error loading exchanges:', err)
    });
  }

  completeExchangeById(exchangeId: number) {
    this.apiService.completeExchange(exchangeId).subscribe({
      next: () => {
        this.showExchangeMessage(`Exchange #${exchangeId} completed.`);
        this.loadMyExchanges();
        this.loadMatches();
        this.loadUserSkills();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.exchangeMessage = 'Failure: ' + (err.error?.message || err.message);
        this.cdr.detectChanges();
      }
    });
  }

    cancelExchangeById(exchangeId: number) {
    if (!this.currentUser?.id) return;
    if (!confirm('Are you sure you want to cancel this skillSwap?')) return;

    this.apiService.cancelExchange(exchangeId, this.currentUser.id).subscribe({
      next: () => {
        this.showExchangeMessage(`Exchange #${exchangeId} cancelled.`);
        this.loadMyExchanges();
        this.loadMatches();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.exchangeMessage = 'Failure: ' + (err.error?.message || err.message);
        this.cdr.detectChanges();
      }
    });
  }

    deleteExchangeById(exchangeId: number) {
    if (!this.currentUser?.id) return;
    if (!confirm('Delete this cancelled exchange permanently?')) return;

    this.apiService.deleteExchange(exchangeId, this.currentUser.id).subscribe({
      next: () => {
        this.showExchangeMessage(`Exchange #${exchangeId} deleted.`);
        this.loadMyExchanges();
      },
      error: (err) => {
        console.error(err);
        this.exchangeMessage = 'Failure: ' + (err.error?.message || err.message);
        this.cdr.detectChanges();
      }
    });
  }

  // ==========================================
  // REVIEWS
  // ==========================================
  userReviews: any[] = [];
  newReview = {
    exchangeId: null as number | null,
    score: 5,
    comment: ''
  };
  reviewMessage: string = '';

  loadMyReviews() {
    if (!this.currentUser?.id) return;
    this.apiService.getUserReviews(this.currentUser.id).subscribe({
      next: (res) => {
        this.userReviews = res;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error loading reviews:', err)
    });
  }

  submitReview() {
    if (!this.newReview.exchangeId || !this.currentUser?.id) {
      this.reviewMessage = 'Provide the ID of a (COMPLETED) exchange.';
      return;
    }
    const payload = {
      exchangeId: this.newReview.exchangeId,
      reviewerId: this.currentUser.id,
      score: this.newReview.score,
      comment: this.newReview.comment
    };
    this.apiService.createReview(payload).subscribe({
      next: () => {
        this.reviewMessage = 'Review has been submitted!';
        this.newReview = { exchangeId: null, score: 5, comment: '' };
        this.loadMyReviews();
      },
      error: (err) => {
        console.error('Error submitting review:', err);
        this.reviewMessage = 'Failure: ' + (err.error?.message || err.message);
      }
    });
  }

  // ==========================================
  // PROFILE
  // ==========================================
  isEditingProfile = false;
  profileForm = { name: '', email: '' };
  profileMessage: string = '';

  startEditProfile() {
    this.isEditingProfile = true;
    this.profileMessage = '';
  }

  cancelEditProfile() {
    this.isEditingProfile = false;
    if (this.currentUser) {
      this.profileForm = { name: this.currentUser.name, email: this.currentUser.email };
    }
  }

  saveProfile() {
    if (!this.currentUser?.id) return;
    this.apiService.updateUser(this.currentUser.id, this.profileForm).subscribe({
      next: () => {
        this.currentUser = { id: this.currentUser!.id, ...this.profileForm };
        localStorage.setItem('userName', this.profileForm.name);
        localStorage.setItem('userEmail', this.profileForm.email);
        this.isEditingProfile = false;
        this.profileMessage = 'Profile has been updated!';
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error updating profile:', err);
        this.profileMessage = 'Failed to update profile.';
      }
    });
  }

  deleteAccount() {
    if (!this.currentUser?.id) return;
    if (!confirm('Are you sure you want to delete your account? This action cannot be undone.')) return;

    this.apiService.deleteUser(this.currentUser.id).subscribe({
      next: () => this.logout(),
      error: (err) => console.error('Error deleting account:', err)
    });
  }

   proposeThreeWayExchange(match: any) {
    if (!this.currentUser?.id) return;
    const key = this.threeWayKey(match);
    if (this.proposedThreeWayKeys.has(key)) return;
    this.proposedThreeWayKeys.add(key);

    this.apiService.createThreeWayExchange(match).subscribe({
      next: (res: any) => {
        this.showExchangeMessage(`3-Way Exchange proposed successfully!`);
        this.loadMyExchanges();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.proposedThreeWayKeys.delete(key);
        console.error('Error creating 3-way exchange:', err);
        this.exchangeMessage = 'Failed to propose 3-way exchange: ' + (err.error?.message || err.message);
        this.cdr.detectChanges();
      }
    });
  }

  // ==========================================
  // MY RATING
  // ==========================================
  loadMyRating() {
    if (!this.currentUser?.id) return;
    this.apiService.getUserById(this.currentUser.id).subscribe({
      next: (user: any) => {
        if (this.currentUser) {
          this.currentUser.rating = user.rating;
          this.cdr.detectChanges();
        }
      },
      error: (err) => console.error('Error loading rating:', err)
    });
  }
}
