package raihanhori.auction_api.job;

import java.util.Optional;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import raihanhori.auction_api.entity.Auction;
import raihanhori.auction_api.entity.Product;
import raihanhori.auction_api.entity.User;
import raihanhori.auction_api.repository.AuctionRepository;
import raihanhori.auction_api.repository.ProductRepository;
import raihanhori.auction_api.repository.UserRepository;

@Component
public class ChooseAuctionWinnerJob extends QuartzJobBean {
	
	private ProductRepository productRepository;
	
	private AuctionRepository auctionRepository;

	private UserRepository userRepository;

	@Transactional
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		try {
			ApplicationContext applicationContext = (ApplicationContext)
					context.getScheduler().getContext().get("applicationContext");
			
			productRepository = (ProductRepository) applicationContext.getBean(ProductRepository.class);
			auctionRepository = (AuctionRepository) applicationContext.getBean(AuctionRepository.class);
			userRepository = (UserRepository) applicationContext.getBean(UserRepository.class);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
		Long productId = context.getJobDetail().getJobDataMap().getLong("productId");
		
		Optional<Product> hasProduct = productRepository.findById(productId);
		if (hasProduct.isPresent()) {
			Product product = hasProduct.get();
			
			Pageable pageable = PageRequest.of(0, 1, Sort.by("price").descending());
			
			Page<Auction> auctions = auctionRepository.findWinner(productId, pageable);
			if (auctions.getContent().size() > 0) {
				Auction winner = auctions.getContent().get(0);
				
				Optional<User> hasWinnerUser = userRepository.findById(winner.getUser().getId());
				if (hasWinnerUser.isPresent()) {
					User winnerUser = hasWinnerUser.get();
					product.setWinnerUser(winnerUser);
					productRepository.save(product);
				}
			}
		}
	}

}
