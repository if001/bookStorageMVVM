package net.edgwbs.bookstorage.di;

import android.content.Context
import net.edgwbs.bookstorage.repositories.BookRepositoryFactory;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import net.edgwbs.bookstorage.viewModel.BookListViewModel
import javax.inject.Singleton


@Module
class BookRepositoryModule {
    @Provides
    fun provideBookRepositoryModule(context: Context): BookRepositoryFactory {
        return BookRepositoryFactory.getInstance(context)
    }
}

@Singleton
@Component(modules = [BookRepositoryModule::class])
interface BookRepositoryInjector {
    fun inject(bookListViewModel: BookListViewModel)
    @Component.Builder
    interface Builder {
        fun build(): BookRepositoryInjector
    }
}