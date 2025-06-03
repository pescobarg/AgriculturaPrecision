import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Comment {
  id: number;
  author: string;
  content: string;
  date: Date;
}

interface BlogPost {
  id: number;
  title: string;
  author: string;
  content: string;
  date: Date;
  category: 'recommendation' | 'question';
  comments: Comment[];
}

@Component({
  selector: 'app-blog',
  imports: [CommonModule, FormsModule],
  templateUrl: './blog.component.html',
  styleUrl: './blog.component.css'
})
export class BlogComponent {
  posts: BlogPost[] = [
    {
      id: 1,
      title: 'Mejor época para sembrar tomates',
      author: 'María González',
      content: 'Los tomates se siembran mejor en primavera cuando las temperaturas nocturnas no bajan de 15°C. Es importante preparar el suelo con compost orgánico al menos 2 semanas antes de la siembra. Recomiendo variedades como Roma o Cherry para principiantes.',
      date: new Date('2024-03-15'),
      category: 'recommendation',
      comments: [
        {
          id: 1,
          author: 'Carlos Pérez',
          content: '¡Excelente consejo! Yo he tenido muy buenos resultados con tomates cherry siguiendo estas recomendaciones.',
          date: new Date('2024-03-16')
        }
      ]
    },
    {
      id: 2,
      title: '¿Por qué se amarillean las hojas de mi lechuga?',
      author: 'Ana López',
      content: 'Tengo un pequeño huerto en casa y las hojas de mi lechuga se están poniendo amarillas. ¿Alguien sabe qué puede estar pasando? Riego cada dos días y está en un lugar con buena luz.',
      date: new Date('2024-03-10'),
      category: 'question',
      comments: [
        {
          id: 2,
          author: 'José Martín',
          content: 'Puede ser exceso de riego. La lechuga prefiere suelo húmedo pero no encharcado. También revisa si tiene buen drenaje.',
          date: new Date('2024-03-11')
        },
        {
          id: 3,
          author: 'Laura Sánchez',
          content: 'También podría ser falta de nitrógeno. Prueba con un fertilizante orgánico rico en nitrógeno.',
          date: new Date('2024-03-12')
        }
      ]
    },
    {
      id: 3,
      title: 'Cultivo de zanahorias en macetas',
      author: 'Pedro Ramírez',
      content: 'Para quienes tienen poco espacio, las zanahorias pueden cultivarse perfectamente en macetas profundas (mínimo 30cm). Usar variedades cortas como Chantenay. El suelo debe ser suelto y sin piedras para evitar deformaciones.',
      date: new Date('2024-03-08'),
      category: 'recommendation',
      comments: []
    }
  ];

  // Formulario para nueva publicación
  newPost = {
    title: '',
    author: '',
    content: '',
    category: 'recommendation' as 'recommendation' | 'question'
  };

  // Formulario para comentarios
  newComment = {
    author: '',
    content: '',
    postId: 0
  };

  showNewPostForm = false;
  showCommentForm: { [key: number]: boolean } = {};

  toggleNewPostForm() {
    this.showNewPostForm = !this.showNewPostForm;
    if (!this.showNewPostForm) {
      this.resetNewPostForm();
    }
  }

  toggleCommentForm(postId: number) {
    this.showCommentForm[postId] = !this.showCommentForm[postId];
    if (!this.showCommentForm[postId]) {
      this.resetCommentForm();
    }
  }

  createPost() {
    if (this.newPost.title && this.newPost.author && this.newPost.content) {
      const post: BlogPost = {
        id: this.posts.length + 1,
        title: this.newPost.title,
        author: this.newPost.author,
        content: this.newPost.content,
        date: new Date(),
        category: this.newPost.category,
        comments: []
      };
      
      this.posts.unshift(post); // Agregar al inicio
      this.resetNewPostForm();
      this.showNewPostForm = false;
    }
  }

  addComment(postId: number) {
    if (this.newComment.author && this.newComment.content) {
      const post = this.posts.find(p => p.id === postId);
      if (post) {
        const comment: Comment = {
          id: Date.now(), // Simple ID basado en timestamp
          author: this.newComment.author,
          content: this.newComment.content,
          date: new Date()
        };
        
        post.comments.push(comment);
        this.resetCommentForm();
        this.showCommentForm[postId] = false;
      }
    }
  }

  resetNewPostForm() {
    this.newPost = {
      title: '',
      author: '',
      content: '',
      category: 'recommendation'
    };
  }

  resetCommentForm() {
    this.newComment = {
      author: '',
      content: '',
      postId: 0
    };
  }

  getCategoryLabel(category: string): string {
    return category === 'recommendation' ? 'Recomendación' : 'Pregunta';
  }

  getCategoryClass(category: string): string {
    return category === 'recommendation' ? 'category-recommendation' : 'category-question';
  }
}