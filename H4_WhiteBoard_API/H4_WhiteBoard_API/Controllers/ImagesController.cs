using Microsoft.AspNetCore.Mvc;
using System.Drawing;
using System.Text;

namespace H4_WhiteBoard_API.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class ImagesController : ControllerBase
    {
        string[] paths = new string[]
        {
            "./Images/img001.jpg",
            "./Images/img002.jpg",
            "./Images/img003.jpg"

        };

        static List<Image> imagesFromApp = new List<Image>();

        [HttpGet]
        public string Get()
        {
            using (Image image = Image.FromFile(paths[1]))
            {
                using (MemoryStream m = new MemoryStream())
                {
                    image.Save(m, image.RawFormat);
                    byte[] imageBytes = m.ToArray();

                    // Convert byte[] to Base64 String
                    return Convert.ToBase64String(imageBytes);
                }
            }

        }

        [HttpPost("SaveImages")]
        public IActionResult SaveImage([FromForm] string base64Image)
        {
            try
            {
                byte[] imageBytes = Convert.FromBase64String(base64Image);
                using (MemoryStream ms = new MemoryStream(imageBytes))
                {
                    imagesFromApp.Add(Image.FromStream(ms));
                    return Ok();
                }
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
    }
}
